package main.server.repo.location;

import main.server.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepo extends JpaRepository<Location, Long> {
}
