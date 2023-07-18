package main.server.mapper.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.event.EventDto;
import main.server.dto.event.EventShortDto;
import main.server.dto.event.NewEventDto;
import main.server.mapper.category.CategoryMapper;
import main.server.mapper.user.UserMapper;
import main.server.model.event.Event;
import main.server.model.request.Request;
import main.server.model.request.RequestStatus;
import stats.client.StatsClient;
import stats.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event mapDtoToModel(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setPaid(dto.getPaid() != null && dto.getPaid());
        event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setParticipationLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        return event;
    }

    public static EventDto mapModelToFullDto(Event event, StatsClient statsClient) {
        return EventDto.builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(calculateRequests.apply(event.getRequests()))
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .state(event.getEventStatus())
                .title(event.getTitle())
                .views(calculateViewsOfEvent.apply(event.getEventId(), statsClient))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .location(LocationMapper.mapModelToDto(event.getLocation()))
                .participantLimit(event.getParticipationLimit())
                .publishedOn(event.getPublishedOn() == null ? "" : event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto mapModelToShortDto(Event event, StatsClient statsClient) {
        return EventShortDto.builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(calculateRequests.apply(event.getRequests()))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(calculateViewsOfEvent.apply(event.getEventId(), statsClient))
                .build();
    }

    static final Function<List<Request>, Integer> calculateRequests = list ->
            list == null || list.isEmpty() ? 0 : (int) list.stream()
                    .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED)).count();
    static final BiFunction<Long, StatsClient, Integer> calculateViewsOfEvent = (id, statsClient) -> {
        StatsResponseDto stats = statsClient.getStats("2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                new String[]{String.format("/events/%d", id)}, "true").blockFirst();
        return stats == null ? 0 : stats.getHits().intValue();
    };
}
