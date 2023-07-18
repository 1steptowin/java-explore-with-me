package main.server.dto.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventRequest {
    @Length(max = 64)
    String title;
    @Length(max = 4096)
    String description;
    @Length(max = 1024)
    String annotation;
    String eventDate;
    Integer category;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
}
