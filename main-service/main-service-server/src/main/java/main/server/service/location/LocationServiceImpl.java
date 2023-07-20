package main.server.service.location;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.EventShortDto;
import main.server.dto.location.LocationDto;
import main.server.exception.location.LocationNotFoundException;
import main.server.exception.user.UserNotFoundException;
import main.server.mapper.event.EventMapper;
import main.server.mapper.location.LocationMapper;
import main.server.model.event.EventStatus;
import main.server.model.location.Location;
import main.server.repo.event.EventRepo;
import main.server.repo.location.LocationRepo;
import main.server.repo.user.UserRepo;
import org.springframework.stereotype.Service;
import stats.client.StatsClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationServiceImpl implements LocationService {
    UserRepo userRepo;
    LocationRepo locationRepo;
    EventRepo eventRepo;
    StatsClient statsClient;


    @Override
    public LocationDto addLocation(LocationDto locationDto) {
        return LocationMapper.mapModelToDto(locationRepo.save(LocationMapper.mapDtoToModel(locationDto)));
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepo.findAll().stream().map(LocationMapper::mapModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsAroundLocation(Long locationId) {
        Location location = locationRepo.findById(locationId).orElseThrow(() -> {
            throw new LocationNotFoundException(String.format("Location %d not found", locationId));
        });
        return eventRepo.findEventsAround(location.getLongitude(),location.getLatitude(),location.getRadius())
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsAroundUserLocation(Long userId, LocationDto userLocation, Float radius) {
        checkIfUserExists(userId);
        return eventRepo.findEventsAround(userLocation.getLongitude(), userLocation.getLatitude(), radius)
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format("User %d not found", userId));
        }
    }
}
