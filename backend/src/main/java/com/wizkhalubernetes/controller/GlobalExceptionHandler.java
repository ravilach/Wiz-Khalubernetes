// Global exception handler for friendly error responses
package com.wizkhalubernetes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler for friendly error responses.
 * Handles uncaught exceptions and returns JSON error details.
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all uncaught exceptions in the application and returns a friendly JSON error response.
     * @param ex The exception that was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        logger.debug("GlobalExceptionHandler caught exception: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("error", "An unexpected error occurred. Please try again later.");
        error.put("details", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
