package main.server.mapper.featureLocation;

import main.server.dto.featureLocation.FeatureLocationDto;
import main.server.model.featureLocation.FeatureLocation;

public class FeatureLocationMapper {
    public static FeatureLocation mapDtoToModel(FeatureLocationDto dto) {
        FeatureLocation featureLocation = new FeatureLocation();
        featureLocation.setName(dto.getName());
        featureLocation.setLatitude(dto.getLatitude());
        featureLocation.setLongitude(dto.getLongitude());
        featureLocation.setRadius(dto.getRadius());
        return featureLocation;
    }

    public static FeatureLocationDto mapModelToDto(FeatureLocation model) {
        return FeatureLocationDto.builder()
                .locationId(model.getLocationId())
                .name(model.getName())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .radius(model.getRadius())
                .build();
    }
}
