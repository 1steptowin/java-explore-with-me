package main.server.service.featureLocation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.EventShortDto;
import main.server.dto.event.LocationDto;
import main.server.dto.featureLocation.FeatureLocationDto;
import main.server.exception.featureLocation.FeatureLocationNotFoundException;
import main.server.exception.user.UserNotFoundException;
import main.server.mapper.event.EventMapper;
import main.server.mapper.featureLocation.FeatureLocationMapper;
import main.server.model.event.EventStatus;
import main.server.model.featureLocation.FeatureLocation;
import main.server.repo.event.EventRepo;
import main.server.repo.featureLocation.FeatureLocationRepo;
import main.server.repo.user.UserRepo;
import org.springframework.stereotype.Service;
import stats.client.StatsClient;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeatureLocationServiceImpl implements FeatureLocationService {
    UserRepo userRepo;
    FeatureLocationRepo locationRepo;
    EventRepo eventRepo;
    StatsClient statsClient;
    @Override
    public FeatureLocationDto addLocation(FeatureLocationDto locationDto) {
        return FeatureLocationMapper.mapModelToDto(locationRepo.save(FeatureLocationMapper.mapDtoToModel(locationDto)));
    }

    @Override
    public List<FeatureLocationDto> getAllLocations() {
        return locationRepo.findAll().stream().map(FeatureLocationMapper::mapModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsAroundLocation(Long locationId) {
        FeatureLocation featureLocation = locationRepo.findById(locationId).orElseThrow(() -> {
            throw new FeatureLocationNotFoundException(String.format("Location %d has not been added by admin", locationId));
        });
        return eventRepo.findEventsAround(featureLocation.getLongitude(), featureLocation.getLatitude(),
                        featureLocation.getRadius())
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsAroundUsersLocation(Long userId, LocationDto usersLocation, Long radius) {
        checkIfUserExists(userId);
        return eventRepo.findEventsAround(usersLocation.getLon(), usersLocation.getLat(), radius.doubleValue())
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format("User %d does not exist", userId));
        }
    }
}
