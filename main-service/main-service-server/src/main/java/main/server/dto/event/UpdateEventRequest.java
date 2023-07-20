package main.server.dto.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventRequest {
    @Length(min = 3, max = 120)
    String title;
    @Length(min = 20, max = 7000)
    String description;
    @Length(min = 20, max = 2000)
    String annotation;
    String eventDate;
    Integer category;
    EventLocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
}
