package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.EventShortDto;
import main.server.dto.location.LocationDto;
import main.server.service.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationController {
    LocationService service;

    @Autowired
    public LocationController(LocationService service) {
        this.service = service;
    }

    @PostMapping(value = PathsConstants.LOCATION_ADMIN_PATH)
    public ResponseEntity<LocationDto> addLocation(@Valid @RequestBody LocationDto locationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addLocation(locationDto));
    }

    @GetMapping(value = PathsConstants.LOCATION_ADMIN_PATH)
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok().body(service.getAllLocations());
    }

    @GetMapping(value = PathsConstants.LOCATION_PUBLIC_PATH)
    public ResponseEntity<EventShortDto> getEventsAroundLocation(@PathVariable("locationId") Long locationId) {
        return ResponseEntity.ok().body(service.getEventsAroundLocation(locationId));
    }

    @GetMapping(value = PathsConstants.LOCATION_PRIVATE_PATH)
    public ResponseEntity<List<EventShortDto>> getEventsAroundUserLocation(@PathVariable("userId") Long userId,
                                                                           @Valid @RequestBody LocationDto userLocation,
                                                                           @RequestParam(name = "radius", required = false, defaultValue = "10") Float radius) {
        return ResponseEntity.ok().body(service.getEventsAroundUserLocation(userId, userLocation, radius));
    }
}
