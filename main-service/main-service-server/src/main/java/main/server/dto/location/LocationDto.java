package main.server.dto.location;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {
    Long locationId;
    @NotBlank
    String name;
    @NotNull
    Double longitude;
    @NotNull
    Double latitude;
    @NotNull
    Double radius;
}
