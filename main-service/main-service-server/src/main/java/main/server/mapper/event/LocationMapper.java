package main.server.mapper.event;

import main.server.dto.event.LocationDto;
import main.server.model.event.Location;

public class LocationMapper {
    public static Location mapDtoToModel(LocationDto dto) {
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    public static LocationDto mapModelToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
