package main.server.dto.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Length(max = 64)
    String title;
    @NotBlank
    @Length(max = 4096)
    String description;
    @NotBlank
    @Length(max = 1024)
    String annotation;
    String eventDate;
    Integer category;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
}
