package com.jlmasa.pms_backend.common;

import com.jlmasa.pms_backend.task.service.InvalidTaskTransitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock){
        this.clock = clock;
    }
    @ExceptionHandler(InvalidTaskTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidTransition(InvalidTaskTransitionException ex, jakarta.servlet.http.HttpServletRequest request){
        ApiError body = new ApiError(clock.instant(), 409, "INVALID_STATE_TRANSITION", ex.getMessage(), request.getRequestURI(), org.slf4j.MDC.get("correlationId"));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
