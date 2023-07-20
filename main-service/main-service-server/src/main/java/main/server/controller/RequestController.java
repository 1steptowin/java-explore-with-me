package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.request.RequestDto;
import main.server.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(PathsConstants.REQUESTS_BY_USER_ID_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {
    RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<RequestDto> addRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam("eventId") Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.addRequest(userId, eventId));
    }

    @PatchMapping(value = PathsConstants.CANCEL_OWN_REQUEST_PATH)
    public ResponseEntity<RequestDto> cancelOwnRequest(@PathVariable("userId") Long userId,
                                                                    @PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok().body(requestService.cancelOwnRequest(userId, requestId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getUsersRequests(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(requestService.getUsersRequests(userId));
    }
}
