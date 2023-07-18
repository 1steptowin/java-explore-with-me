package main.server.repo.request;

import main.server.model.request.Request;
import main.server.model.request.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepo extends JpaRepository<Request, Long>,
        QuerydslPredicateExecutor<Request> {
    List<Request> findAllByRequestStatusAndEvent_EventId(RequestStatus requestStatus, Long eventId);

    Optional<Request> findByRequester_UserIdAndEvent_EventId(Long requesterId, Long eventId);
}
