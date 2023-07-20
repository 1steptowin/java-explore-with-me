package main.server.service.featureLocation;

import main.server.dto.event.EventShortDto;
import main.server.dto.event.LocationDto;
import main.server.dto.featureLocation.FeatureLocationDto;

import java.util.List;

public interface FeatureLocationService {
    FeatureLocationDto addLocation(FeatureLocationDto locationDto);

    List<FeatureLocationDto> getAllLocations();

    List<EventShortDto> getEventsAroundLocation(Long locationId);

    List<EventShortDto> getEventsAroundUsersLocation(Long userId, LocationDto usersLocation, Long radius);
}
