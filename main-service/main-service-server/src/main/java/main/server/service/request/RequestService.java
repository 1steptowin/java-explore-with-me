package main.server.service.request;

import main.server.dto.request.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    RequestDto cancelOwnRequest(Long userId, Long requestId);

    List<RequestDto> getUsersRequests(Long userId);
}
