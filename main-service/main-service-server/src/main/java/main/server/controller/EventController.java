package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.*;
import main.server.dto.request.RequestDto;
import main.server.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stats.client.StatsClient;
import stats.dto.StatsRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
    EventService eventService;
    StatsClient statsClient;
    String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public EventController(EventService eventService, StatsClient statsClient) {
        this.eventService = eventService;
        this.statsClient = statsClient;
    }

    @PostMapping(value = PathsConstants.EVENT_PRIVATE_PATH)
    public ResponseEntity<EventDto> addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(userId, newEventDto));
    }

    @PatchMapping(value = PathsConstants.EVENT_ADMIN_BY_ID_PATH)
    public ResponseEntity<EventDto> updateEventAdmin(@PathVariable("eventId") Long eventId,
                                                     @Valid @RequestBody UpdateEventRequest updateRequest) {
        return ResponseEntity.ok().body(eventService.updateEventAdmin(eventId, updateRequest));
    }

    @PatchMapping(value = PathsConstants.EVENT_PRIVATE_BY_ID_PATH)
    public ResponseEntity<EventDto> updateEventPrivate(@PathVariable("userId") Long userId,
                                                           @PathVariable("eventId") Long eventId,
                                                           @Valid @RequestBody UpdateEventRequest updateRequest) {
        return ResponseEntity.ok().body(eventService.updateEventPrivate(userId, eventId, updateRequest));
    }

    @GetMapping(PathsConstants.EVENT_ADMIN_PATH)
    public ResponseEntity<List<EventDto>> searchEventsAdmin(@RequestParam(name = "users", required = false) Optional<Integer[]> users,
                                                                @RequestParam(name = "states", required = false) Optional<String[]> states,
                                                                @RequestParam(name = "categories", required = false) Optional<Integer[]> categories,
                                                                @RequestParam(name = "rangeStart", required = false) Optional<String> rangeStart,
                                                                @RequestParam(name = "rangeEnd", required = false) Optional<String> rangeEnd,
                                                                @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok().body(eventService.searchEventsAdmin(users, states, categories, rangeStart, rangeEnd,
                from, size));
    }

    @GetMapping(PathsConstants.EVENT_PRIVATE_PATH)
    public ResponseEntity<List<EventShortDto>> getAllUsersEventsPrivate(@PathVariable("userId") Long userId,
                                                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok().body(eventService.getAllUsersEvents(userId, from, size));
    }

    @GetMapping(PathsConstants.EVENT_PUBLIC_PATH)
    public ResponseEntity<List<EventShortDto>> searchEventsPublic(@RequestParam(name = "text", required = false) Optional<String> text,
                                                                  @RequestParam(name = "categories", required = false) Optional<Integer[]> categories,
                                                                  @RequestParam(name = "paid", required = false) Optional<Boolean> paid,
                                                                  @RequestParam(name = "rangeStart", required = false) Optional<String> rangeStart,
                                                                  @RequestParam(name = "rangeEnd", required = false) Optional<String> rangeEnd,
                                                                  @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                                                  @RequestParam(name = "sort", required = false, defaultValue = "EVENT_DATE") String sort,
                                                                  @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        statsClient.saveRecord(StatsRequestDto.builder()
                .uri("/events")
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimePattern)))
                .build()).block();
        return ResponseEntity.ok().body(eventService.searchEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size));
    }

    @GetMapping(PathsConstants.EVENT_PUBLIC_BY_ID_PATH)
    public ResponseEntity<EventDto> getEventByIdPublic(@PathVariable("eventId") Long id) {
        statsClient.saveRecord(StatsRequestDto.builder()
                .uri(String.format("/events/%d", id))
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimePattern)))
                .build()).block();
        return ResponseEntity.ok().body(eventService.getEventByIdPublic(id));
    }

    @GetMapping(PathsConstants.EVENT_PRIVATE_BY_ID_PATH)
    public ResponseEntity<EventDto> getEventByIdPrivate(@PathVariable("userId") Long userId,
                                                            @PathVariable("eventId") Long eventId) {
        return ResponseEntity.ok().body(eventService.getEventByIdPrivate(userId, eventId));
    }

    @PatchMapping(value = PathsConstants.EVENT_PRIVATE_BY_ID_AND_REQUEST_PATH)
    public ResponseEntity<EventStatusUpdateResponse> updateRequestByInitiator(@PathVariable("userId") Long userId,
                                                                              @PathVariable("eventId") Long eventId,
                                                                              @Valid @RequestBody EventStatusUpdateRequest request) {
        return ResponseEntity.ok().body(eventService.updateRequestByInitiator(userId, eventId, request));
    }

    @GetMapping(value = PathsConstants.EVENT_PRIVATE_BY_ID_AND_REQUEST_PATH)
    public ResponseEntity<List<RequestDto>> getRequestsToUsersEvent(@PathVariable("userId") Long userId,
                                                                    @PathVariable("eventId") Long eventId) {
        return ResponseEntity.ok().body(eventService.getRequestsToUsersEvent(userId, eventId));
    }
}
