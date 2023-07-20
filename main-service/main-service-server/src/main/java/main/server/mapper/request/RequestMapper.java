package main.server.mapper.request;

import main.server.dto.request.RequestDto;
import main.server.model.request.Request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static RequestDto mapModelToDto(Request request) {
        return RequestDto.builder()
                .id(request.getRequestId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requester(request.getRequester().getUserId())
                .event(request.getEvent().getEventId())
                .status(request.getRequestStatus().toString())
                .build();
    }
}
