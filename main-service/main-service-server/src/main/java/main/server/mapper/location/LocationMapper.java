package main.server.mapper.location;

import main.server.dto.location.LocationDto;
import main.server.model.location.Location;

public class LocationMapper {
    public static LocationDto mapModelToDto(Location location) {
        return LocationDto.builder()
                .locationId(location.getLocationId())
                .name(location.getName())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .radius(location.getRadius())
                .build();
    }

    public static Location mapDtoToModel(LocationDto locationDto) {
        Location location = new Location();
        location.setName(locationDto.getName());
        location.setLongitude(locationDto.getLongitude());
        location.setLatitude(locationDto.getLatitude());
        location.setRadius(locationDto.getRadius());
        return location;
    }
}
