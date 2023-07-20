package main.server.repo.event;

import main.server.model.event.EventLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLocationRepo extends JpaRepository<EventLocation, Long> {
}
