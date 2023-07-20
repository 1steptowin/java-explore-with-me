package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.service.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationController {
    LocationService service;

    @Autowired
    public LocationController(LocationService service) {
        this.service = service;
    }
}
