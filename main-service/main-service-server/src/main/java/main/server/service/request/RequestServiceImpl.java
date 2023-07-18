package main.server.service.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.server.dto.request.RequestDto;
import main.server.exception.event.EventNotFoundException;
import main.server.exception.request.IllegalRequestException;
import main.server.exception.request.RequestNotFoundException;
import main.server.exception.user.UserNotFoundException;
import main.server.mapper.request.RequestMapper;
import main.server.model.event.Event;
import main.server.model.event.EventStatus;
import main.server.model.request.Request;
import main.server.model.request.RequestStatus;
import main.server.model.request.QRequest;
import main.server.repo.event.EventRepo;
import main.server.repo.request.RequestRepo;
import main.server.repo.user.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepo requestRepo;
    UserRepo userRepo;
    EventRepo eventRepo;

    @Transactional
    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        checkIfRequestWasAlreadyCreated(userId, eventId);
        checkIfInitiatorIsCreatingRequest(userId, eventId);
        Request newRequest = new Request();
        Event eventFound = eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        });
        checkIfEventIsPublished(eventFound);
        checkIfParticipantLimitIsFull(eventFound);
        newRequest.setRequester(userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        }));
        newRequest.setEvent(eventFound);
        if (eventFound.getParticipationLimit() == 0 || !eventFound.getRequestModeration()) {
            newRequest.setRequestStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setRequestStatus(RequestStatus.PENDING);
        }
        newRequest.setCreated(LocalDateTime.now());
        return RequestMapper.mapModelToDto(requestRepo.save(newRequest));
    }

    @Transactional
    @Override
    public RequestDto cancelOwnRequest(Long userId, Long requestId) {
        checkIfUserExists(userId);
        Request canceled = requestRepo.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException("Request does not exist");
        });
        canceled.setRequestStatus(RequestStatus.CANCELED);
        return RequestMapper.mapModelToDto(requestRepo.save(canceled));
    }

    @Override
    public List<RequestDto> getUsersRequests(Long userId) {
        QRequest qRequest = QRequest.request;
        BooleanExpression byRequesterId = qRequest.requester.userId.eq(userId);
        return StreamSupport.stream(requestRepo.findAll(byRequesterId).spliterator(), false)
                .map(RequestMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }


    private void checkIfParticipantLimitIsFull(Event eventFound) {
        if (eventFound.getParticipationLimit() != 0) {
            if (eventFound.getParticipationLimit() == eventFound.getRequests().stream()
                    .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED)).count()) {
                throw new IllegalRequestException("Error participant limit");
            }
        }
    }

    private void checkIfEventIsPublished(Event event) {
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalRequestException("Event has not been published yet");
        }
    }

    private void checkIfInitiatorIsCreatingRequest(Long userId, Long eventId) {
        if (Objects.equals(eventRepo.findById(eventId).orElseThrow().getInitiator().getUserId(), userId)) {
            throw new IllegalRequestException("Initiator may not create request to participate in his own event");
        }
    }

    private void checkIfRequestWasAlreadyCreated(Long userId, Long eventId) {
        if (requestRepo.findByRequester_UserIdAndEvent_EventId(userId, eventId).isPresent()) {
            throw new IllegalRequestException("Request was already created");
        }
    }
}
