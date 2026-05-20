package com.sportsclub.infrastructure.adapter.in.rest;

import com.sportsclub.shared.domain.exception.AthleteNotFoundException;
import com.sportsclub.shared.domain.exception.DomainException;
import com.sportsclub.shared.domain.exception.InvalidSearchParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AthleteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAthleteNotFound(AthleteNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("errorCode", "ATHLETE_NOT_FOUND");
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidSearchParameterException.class)
    public ResponseEntity<Map<String, String>> handleInvalidSearch(InvalidSearchParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("errorCode", "INVALID_SEARCH_PARAMETER");
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException ex) {
        logger.warn("Domain exception: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("sporttype")) { error.put("errorCode", "INVALID_SPORT_TYPE"); error.put("error", "Sport type must be GYM or FOOTBALL"); }
        else if (message != null && message.toLowerCase().contains("uuid")) { error.put("errorCode", "INVALID_ATHLETE_ID"); error.put("error", "Athlete ID must be a valid UUID"); }
        else { error.put("errorCode", "INVALID_REQUEST"); error.put("error", message); }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) { error.put("errorCode", "INVALID_ATHLETE_ID"); error.put("error", "Athlete ID must be a valid UUID"); }
        else { error.put("errorCode", "INVALID_REQUEST"); error.put("error", "Invalid parameter format"); }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("INTERNAL SERVER ERROR", ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}