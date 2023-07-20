package main.server.model.featureLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "feature_locations", schema = "public")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeatureLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    Long locationId;
    @Column(name = "name")
    String name;
    @Column(name = "latitude")
    Double latitude;
    @Column(name = "longitude")
    Double longitude;
    @Column(name = "radius")
    Double radius;
}
