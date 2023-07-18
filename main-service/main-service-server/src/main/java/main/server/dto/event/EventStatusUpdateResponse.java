package main.server.dto.event;

import main.server.dto.request.RequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventStatusUpdateResponse {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
