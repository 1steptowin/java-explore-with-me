package stats.server.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ErrorResponse {
    String error;

    public ErrorResponse(final String error) {
        this.error = error;
    }
}
