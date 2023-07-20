package main.server.mapper.event;

import main.server.dto.event.EventLocationDto;
import main.server.model.event.EventLocation;

public class EventLocationMapper {
    public static EventLocation mapDtoToModel(EventLocationDto dto) {
        EventLocation location = new EventLocation();
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        return location;
    }

    public static EventLocationDto mapModelToDto(EventLocation location) {
        return EventLocationDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
