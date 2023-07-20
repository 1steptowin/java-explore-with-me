package main.server.service.event;

import main.server.dto.event.*;
import main.server.dto.event.*;
import main.server.dto.request.RequestDto;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventDto addEvent(Long userId, NewEventDto newEventDto);

    EventDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest);

    EventDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateRequest);

    List<EventDto> searchEventsAdmin(Optional<Integer[]> users, Optional<String[]> states,
                                         Optional<Integer[]> categories, Optional<String> rangeStart,
                                         Optional<String> rangeEnd, int from, int size);

    List<EventShortDto> getAllUsersEvents(Long userId, int from, int size);

    List<EventShortDto> searchEventsPublic(Optional<String> text, Optional<Integer[]> categories, Optional<Boolean> paid,
                                           Optional<String> rangeStart, Optional<String> rangeEnd,
                                           Boolean onlyAvailable, String sort, int from, int size);

    EventDto getEventByIdPublic(Long id);

    EventDto getEventByIdPrivate(Long userId, Long eventId);

    EventStatusUpdateResponse updateRequestByInitiator(Long userId, Long eventId,
                                                       EventStatusUpdateRequest request);

    List<RequestDto> getRequestsToUsersEvent(Long userId, Long eventId);
}
