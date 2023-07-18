package main.server.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Error {
    HttpStatus status;
    String error;
}
