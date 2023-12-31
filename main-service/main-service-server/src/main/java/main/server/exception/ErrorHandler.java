package main.server.exception;

import main.server.exception.category.CategoryNotFoundException;
import main.server.exception.compilation.CompilationNotFoundException;
import main.server.exception.event.EventNotFoundException;
import main.server.exception.event.IllegalPublicationException;
import main.server.exception.featureLocation.FeatureLocationNotFoundException;
import main.server.exception.request.IllegalRequestException;
import main.server.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            CategoryNotFoundException.class,
            EventNotFoundException.class,
            CompilationNotFoundException.class,
            FeatureLocationNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseException handleNotFoundExceptions(final RuntimeException e) {
        log.error(e.getMessage());
        return ResponseException.builder()
                .error(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler({
            IllegalPublicationException.class,
            IllegalRequestException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseException handeConflicts(final RuntimeException e) {
        log.error(e.getMessage());
        return ResponseException.builder()
                .error(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseException handleUniqueConstraintViolation(final DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return ResponseException.builder()
                .error(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseException handleException(final Exception e) {
        log.error(e.getMessage());
        return ResponseException.builder()
                .error(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
