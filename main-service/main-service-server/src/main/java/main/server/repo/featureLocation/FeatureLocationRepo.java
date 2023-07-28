package main.server.repo.featureLocation;

import main.server.model.featureLocation.FeatureLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureLocationRepo extends JpaRepository<FeatureLocation, Long> {
}
