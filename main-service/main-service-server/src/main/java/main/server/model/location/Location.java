package main.server.model.location;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "locations", schema = "public")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    Long locationId;
    @Column(name = "name")
    String name;
    @Column(name = "longitude")
    Float longitude;
    @Column(name = "latitude")
    Float latitude;
    @Column(name = "radius")
    Float radius;
}
