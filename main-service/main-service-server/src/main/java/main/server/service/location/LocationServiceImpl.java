package main.server.service.location;

import main.server.dto.event.EventShortDto;
import main.server.dto.location.LocationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Override
    public LocationDto addLocation(LocationDto locationDto) {
        return null;
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsAroundLocation(Long locationId) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsAroundUserLocation(Long userId, LocationDto userLocation, Float radius) {
        return null;
    }
}
