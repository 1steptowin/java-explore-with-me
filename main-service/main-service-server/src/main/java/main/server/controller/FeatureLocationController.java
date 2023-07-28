package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.featureLocation.FeatureLocationDto;
import main.server.service.featureLocation.FeatureLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import main.server.dto.event.EventShortDto;
import main.server.dto.event.LocationDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeatureLocationController {
    private static final String ADMIN_LOCATION_PATH = "/admin/locations";
    private static final String PUBLIC_LOCATION_PATH = "/locations/{locationId}";
    private static final String PRIVATE_LOCATION_PATH = "/users/{userId}/locations";
    private final FeatureLocationService featureLocationService;

    @Autowired
    public FeatureLocationController(FeatureLocationService featureLocationService) {
        this.featureLocationService = featureLocationService;
    }

    @PostMapping(value = ADMIN_LOCATION_PATH)
    public ResponseEntity<FeatureLocationDto> addLocation(@Valid @RequestBody FeatureLocationDto locationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(featureLocationService.addLocation(locationDto));
    }

    @GetMapping(ADMIN_LOCATION_PATH)
    public ResponseEntity<List<FeatureLocationDto>> getAllLocations() {
        return ResponseEntity.ok().body(featureLocationService.getAllLocations());
    }

    @GetMapping(PUBLIC_LOCATION_PATH)
    public ResponseEntity<List<EventShortDto>> getEventsAroundLocation(@PathVariable("locationId") Long locationId) {
        return ResponseEntity.ok().body(featureLocationService.getEventsAroundLocation(locationId));
    }

    @GetMapping(PRIVATE_LOCATION_PATH)
    public ResponseEntity<List<EventShortDto>> getEventsAroundUsersLocation(@PathVariable("userId") Long userId,
                                                                            @Valid @RequestBody LocationDto usersLocation,
                                                                            @RequestParam(name = "radius", required = false, defaultValue = "1") Long radius) {
        return ResponseEntity.ok().body(featureLocationService.getEventsAroundUsersLocation(userId, usersLocation, radius));
    }
}
