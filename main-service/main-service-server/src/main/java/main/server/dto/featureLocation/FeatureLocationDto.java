package main.server.dto.featureLocation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeatureLocationDto {
    Long locationId;
    @NotBlank
    String name;
    @NotNull
    Double latitude;
    @NotNull
    Double longitude;
    @NotNull
    Double radius;
}
