package ewm.server.exception;

import ewm.server.exception.category.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            CategoryNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFoundExceptions(final RuntimeException e) {
        log.error(e.getMessage());
        return Error.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
    }
}
