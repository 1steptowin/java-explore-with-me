package main.server.service.location;

import main.server.dto.event.EventShortDto;
import main.server.dto.location.LocationDto;

import java.util.List;

public interface LocationService {
    LocationDto addLocation(LocationDto locationDto);

    List<LocationDto> getAllLocations();

    List<EventShortDto> getEventsAroundLocation(Long locationId);

    List<EventShortDto> getEventsAroundUserLocation(Long userId, LocationDto userLocation, Float radius);
}
