package main.server.model.featureLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = TableAndColumnsNames.tableName, schema = TableAndColumnsNames.defualtSchema)
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeatureLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = TableAndColumnsNames.locationIdColumnName)
    Long locationId;
    @Column(name = TableAndColumnsNames.nameColumnName)
    String name;
    @Column(name = TableAndColumnsNames.latitudeColumnName)
    Double latitude;
    @Column(name = TableAndColumnsNames.longitudeColumnName)
    Double longitude;
    @Column(name = TableAndColumnsNames.radiusColumnName)
    Double radius;
}
