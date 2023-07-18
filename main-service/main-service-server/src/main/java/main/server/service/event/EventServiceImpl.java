package main.server.service.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.*;
import main.server.dto.request.RequestDto;
import main.server.exception.category.CategoryNotFoundException;
import main.server.exception.event.EventNotFoundException;
import main.server.exception.event.IllegalDatesException;
import main.server.exception.event.IllegalPublicationException;
import main.server.exception.event.UnknownActionException;
import main.server.exception.request.IllegalRequestException;
import main.server.exception.user.UserNotFoundException;
import main.server.mapper.event.EventMapper;
import main.server.mapper.event.LocationMapper;
import main.server.mapper.request.RequestMapper;
import main.server.model.category.Category;
import main.server.model.event.Event;
import main.server.model.event.EventStatus;
import main.server.model.event.Location;
import main.server.model.event.QEvent;
import main.server.model.request.Request;
import main.server.model.request.RequestStatus;
import main.server.model.request.QRequest;
import main.server.model.user.User;
import main.server.repo.category.CategoryRepo;
import main.server.repo.event.EventRepo;
import main.server.repo.event.LocationRepo;
import main.server.repo.request.RequestRepo;
import main.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import main.server.service.EWMPageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stats.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    @Value("${date-time.format}")
    String dateTimePattern;
    UserRepo userRepo;
    CategoryRepo categoryRepo;
    EventRepo eventRepo;
    LocationRepo locationRepo;
    RequestRepo requestRepo;
    StatsClient statsClient;

    @Transactional
    @Override
    public EventDto addEvent(Long userId, NewEventDto newEventDto) {
        Event newEvent = EventMapper.mapDtoToModel(newEventDto);
        validateEventDate(newEvent.getEventDate());
        newEvent.setCategory(getCategory(newEventDto.getCategory()));
        newEvent.setLocation(saveLocation(newEventDto.getLocation()));
        newEvent.setInitiator(getInitiator(userId));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setEventStatus(EventStatus.PENDING);
        Event savedEvent = eventRepo.save(newEvent);
        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Transactional
    @Override
    public EventDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest) {
        Event update = getEvent(eventId);
        updateEvent(update, updateRequest);
        updateStatusAdmin(update, updateRequest);
        Event savedEvent = eventRepo.save(update);
        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Transactional
    @Override
    public EventDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateRequest) {
        checkIfUserExists(userId);
        Event update = getEvent(eventId);
        checkIfEventAlreadyPublished(update);
        updateEvent(update, updateRequest);
        updateStatusUser(update, updateRequest);
        Event savedEvent = eventRepo.save(update);
        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Override
    public List<EventDto> searchEventsAdmin(Optional<Integer[]> users, Optional<String[]> states,
                                                Optional<Integer[]> categories, Optional<String> rangeStart,
                                                Optional<String> rangeEnd, int from, int size) {
        BooleanExpression searchExp = makeSearchExpAdmin(users, states, categories, rangeStart, rangeEnd);
        return eventRepo.findAll(searchExp, new EWMPageRequest(from, size)).stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(e -> EventMapper.mapModelToFullDto(e, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Long userId, int from, int size) {
        BooleanExpression byUserId = QEvent.event.initiator.userId.eq(userId);
        return eventRepo.findAll(byUserId, new EWMPageRequest(from, size)).stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> searchEventsPublic(Optional<String> text, Optional<Integer[]> categories,
                                                  Optional<Boolean> paid, Optional<String> rangeStart,
                                                  Optional<String> rangeEnd, Boolean onlyAvailable,
                                                  String sort, int from, int size) {
        BooleanExpression searchExp = makeSearchExpPublic(text, categories, paid, rangeStart, rangeEnd);
        Comparator<EventShortDto> comparator = makeComparator(sort);
        if (onlyAvailable) {
            return eventRepo.findAll(searchExp, new EWMPageRequest(from, size)).stream()
                    .filter(e -> e.getRequests().stream()
                            .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED))
                            .count() < e.getParticipationLimit())
                    .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            return eventRepo.findAll(searchExp, new EWMPageRequest(from, size)).stream()
                    .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventDto getEventByIdPublic(Long id) {
        Event eventFound = eventRepo.findByEventIdAndEventStatus(id, EventStatus.PUBLISHED)
                .orElseThrow(() -> {
                    throw new EventNotFoundException("Event not found");
                });
        return EventMapper.mapModelToFullDto(eventFound, statsClient);
    }

    @Override
    public EventDto getEventByIdPrivate(Long userId, Long eventId) {
        checkIfUserExists(userId);
        Event eventFound = eventRepo.findById(eventId)
                .orElseThrow(() -> {
                    throw new EventNotFoundException("Event not found");
                });
        return EventMapper.mapModelToFullDto(eventFound, statsClient);
    }

    @Transactional
    @Override
    public EventStatusUpdateResponse updateRequestByInitiator(Long userId, Long eventId,
                                                              EventStatusUpdateRequest request) {
        checkIfUserExists(userId);
        RequestStatus status = parseRequestStatus(request.getStatus());
        List<Request> update = makeListOfRequestsToBeUpdated(eventId, request);
        checkIfUpdateRequestIsValid(status, update);
        update.forEach(r -> r.setRequestStatus(status));
        List<Request> updatedRequests = requestRepo.saveAllAndFlush(update);
        rejectPendingRequestsIfParticipantLimitIsReached(updatedRequests);
        return EventStatusUpdateResponse.builder()
                .confirmedRequests(requestRepo.findAllByRequestStatusAndEvent_EventId(RequestStatus.CONFIRMED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(requestRepo.findAllByRequestStatusAndEvent_EventId(RequestStatus.REJECTED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<RequestDto> getRequestsToUsersEvent(Long userId, Long eventId) {
        checkIfUserExists(userId);
        Event eventFound = eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event not found");
        });
        return eventFound.getRequests().stream().map(RequestMapper::mapModelToDto).collect(Collectors.toList());
    }

    private void rejectPendingRequestsIfParticipantLimitIsReached(List<Request> updatedRequests) {
        updatedRequests.stream()
                .filter(r -> r.getEvent().getParticipationLimit() != 0 &&
                        r.getEvent().getParticipationLimit() == r.getEvent().getRequests().stream()
                                .filter(r1 -> r1.getRequestStatus().equals(RequestStatus.CONFIRMED))
                                .count())
                .filter(r -> r.getRequestStatus().equals(RequestStatus.PENDING))
                .forEach(r -> r.setRequestStatus(RequestStatus.REJECTED));
        requestRepo.saveAllAndFlush(updatedRequests);
    }

    private void checkIfUpdateRequestIsValid(RequestStatus status, List<Request> update) {
        if (update.stream().anyMatch(r -> !r.getRequestStatus().equals(RequestStatus.PENDING))) {
            throw new IllegalRequestException("Status may be pending requests only");
        }
        if (status.equals(RequestStatus.CONFIRMED) && update.stream()
                .anyMatch(r -> r.getEvent().getParticipationLimit() != 0 &&
                        r.getEvent().getParticipationLimit() == r.getEvent().getRequests().stream()
                                .filter(r1 -> r1.getRequestStatus().equals(RequestStatus.CONFIRMED))
                                .count())
        ) {
            throw new IllegalRequestException("Error participant limit");
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().minusHours(2).isAfter(eventDate) || eventDate.isBefore(LocalDateTime.now())) {
            throw new IllegalDatesException("Error event date");
        }
    }

    private List<Request> makeListOfRequestsToBeUpdated(Long eventId,
                                                                     EventStatusUpdateRequest request) {
        QRequest qRequest = QRequest.participationRequest;
        BooleanExpression exp = qRequest.event.eventId.eq(eventId)
                .and(qRequest.requestId.in(request.getRequestIds()));
        return StreamSupport.stream(requestRepo.findAll(exp).spliterator(), false)
                .collect(Collectors.toList());
    }

    private RequestStatus parseRequestStatus(String status) {
        try {
            return RequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Status is unknown");
        }
    }

    private Comparator<EventShortDto> makeComparator(String sort) {
        Sort sorting = parseSortType(sort);
        if (sorting.equals(Sort.VIEWS)) {
            return Comparator.comparing(EventShortDto::getViews);
        }
        return Comparator.comparing(EventShortDto::getEventDate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpAdmin(Optional<Integer[]> users, Optional<String[]> states, Optional<Integer[]> categories, Optional<String> rangeStart, Optional<String> rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        users.ifPresent(userIds -> builder.and(qEvent.initiator.userId.in(userIds)));
        states.ifPresent(stateStrings -> builder.and(qEvent.eventStatus.in(Arrays.stream(states.get())
                .map(this::parseEventStatus)
                .toArray(EventStatus[]::new))));
        categories.ifPresent(categoryIds -> builder.and(qEvent.category.categoryId.in(categoryIds)));
        rangeStart.ifPresent(start -> builder.and(qEvent.eventDate.after(parseDateTime(start))));
        rangeEnd.ifPresent(end -> builder.and(qEvent.eventDate.before(parseDateTime(end))));
        return builder.getValue() != null ? Expressions.asBoolean(builder.getValue()) : qEvent.isNotNull();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpPublic(Optional<String> text, Optional<Integer[]> categories, Optional<Boolean> paid, Optional<String> rangeStart, Optional<String> rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.eventStatus.eq(EventStatus.PUBLISHED));
        text.ifPresent(str -> {
            BooleanExpression annotationContainsText = qEvent.annotation.likeIgnoreCase(str);
            BooleanExpression descriptionContainsText = qEvent.description.likeIgnoreCase(str);
            builder.and(annotationContainsText.or(descriptionContainsText));
        });
        categories.ifPresent(categoryIds -> builder.and(qEvent.category.categoryId.in(categoryIds)));
        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            validateSearchDates(rangeStart.get(), rangeEnd.get());
        }
        rangeStart.ifPresent(start -> builder.and(qEvent.eventDate.after(parseDateTime(start))));
        rangeEnd.ifPresent(end -> builder.and(qEvent.eventDate.before(parseDateTime(end))));
        if (rangeStart.isEmpty() || rangeEnd.isEmpty()) {
            builder.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        paid.ifPresent(bool -> builder.and(qEvent.paid.eq(bool)));
        return Expressions.asBoolean(builder.getValue());
    }

    private void validateSearchDates(String rangeStart, String rangeEnd) {
        if (parseDateTime(rangeStart).isAfter(parseDateTime(rangeEnd))) {
            throw new IllegalDatesException("Error range");
        }
    }

    private Location saveLocation(LocationDto locationDto) {
        return locationRepo.save(LocationMapper.mapDtoToModel(locationDto));
    }

    private Category getCategory(Integer categoryId) {
        return categoryRepo.findById(categoryId.longValue()).orElseThrow(() -> {
            throw new CategoryNotFoundException("Category does not exist");
        });
    }

    private User getInitiator(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }

    private Event getEvent(Long eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        });
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void updateEvent(Event update, UpdateEventRequest updateRequest) {
        updateDescription(update, updateRequest);
        updateAnnotation(update, updateRequest);
        updateParticipantLimit(update, updateRequest);
        updateTitle(update, updateRequest);
        updatePaid(update, updateRequest);
        updateCategory(update, updateRequest);
        updateEventDate(update, updateRequest);
        updateLocation(update, updateRequest);
    }

    private void updateStatusAdmin(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getStateAction() != null) {
            AdminAction action = parseActionAdmin(updateRequest.getStateAction());
            switch (action) {
                case PUBLISH_EVENT:
                    checkIfEventAlreadyPublished(update);
                    checkIfEventIsCanceled(update);
                    update.setEventStatus(EventStatus.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    checkIfEventAlreadyPublished(update);
                    update.setEventStatus(EventStatus.CANCELED);
                    break;
            }
        }
    }

    private void checkIfEventIsCanceled(Event update) {
        if (update.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new IllegalPublicationException("Event can not be published");
        }
    }

    private void checkIfEventAlreadyPublished(Event update) {
        if (update.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalPublicationException("Event is already published");
        }
    }

    private void updateStatusUser(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getStateAction() != null) {
            UserAction action = parseActionUser(updateRequest.getStateAction());
            switch (action) {
                case CANCEL_REVIEW:
                    checkIfEventAlreadyPublished(update);
                    update.setEventStatus(EventStatus.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    update.setEventStatus(EventStatus.PENDING);
                    break;
            }
        }
    }

    private AdminAction parseActionAdmin(String stateAction) {
        try {
            return AdminAction.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown action");
        }
    }

    private UserAction parseActionUser(String stateAction) {
        try {
            return UserAction.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown action");
        }
    }

    private void updateLocation(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getLocation() != null) {
            if (!(updateRequest.getLocation().getLat().equals(update.getLocation().getLat()) &&
                    updateRequest.getLocation().getLon().equals(update.getLocation().getLon()))) {
                update.setLocation(saveLocation(updateRequest.getLocation()));
            }
        }
    }

    private void updateEventDate(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getEventDate() != null) {
            LocalDateTime newDateTime = LocalDateTime.parse(updateRequest.getEventDate(),
                    DateTimeFormatter.ofPattern(dateTimePattern));
            validateEventDate(newDateTime);
            update.setEventDate(newDateTime);
        }
    }

    private void updateCategory(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getCategory() != null) {
            if (!(updateRequest.getCategory().longValue() == update.getCategory().getCategoryId())) {
                update.setCategory(getCategory(updateRequest.getCategory()));
            }
        }
    }

    private void updatePaid(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getPaid() != null) {
            update.setPaid(updateRequest.getPaid());
        }
    }

    private void updateTitle(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            update.setTitle(updateRequest.getTitle());
        }
    }

    private void updateParticipantLimit(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getParticipantLimit() != null) {
            update.setParticipationLimit(updateRequest.getParticipantLimit());
        }
    }

    private void updateAnnotation(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            update.setAnnotation(updateRequest.getAnnotation());
        }
    }

    private void updateDescription(Event update, UpdateEventRequest updateRequest) {
        if (updateRequest.getDescription() != null) {
            update.setDescription(updateRequest.getDescription());
        }
    }

    private EventStatus parseEventStatus(String eventStatus) {
        try {
            return EventStatus.valueOf(eventStatus);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown event status");
        }
    }

    private Sort parseSortType(String sort) {
        try {
            return Sort.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown sorting type");
        }
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(dateTimePattern));
    }
}
