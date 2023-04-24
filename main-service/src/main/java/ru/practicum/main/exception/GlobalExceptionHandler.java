package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public Map<String, String> catchResourceNotFoundException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(
                "NOT_FOUND", "The required object was not found.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler({AlreadyExistsException.class, IncorrectRequestException.class, IncorrectEventException.class,
            IncorrectCommentException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException e) {
        log.error(e.getMessage(), e);
        ApiError apiError = new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                Arrays.toString(e.getStackTrace()), HttpStatus.CONFLICT);

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
