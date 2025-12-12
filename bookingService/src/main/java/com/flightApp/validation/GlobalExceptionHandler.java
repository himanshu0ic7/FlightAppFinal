package com.flightApp.validation;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), null);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, Object>> handleBookingException(BookingException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Booking Failed", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid Value",
                        (existing, replacement) -> existing 
          ));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", "Input validation failed", validationErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonErrors(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request";
        
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatEx = (InvalidFormatException) cause;
            if (formatEx.getCause() instanceof RuntimeException) {
                message = formatEx.getCause().getMessage();
            } else {
                message = "Invalid value for field: " + formatEx.getValue();
            }
        } else if (cause instanceof InvalidAirportCodeException) {
             message = cause.getMessage();
        }

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Input", message, null);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignStatusException(FeignException ex) {
        String message = "External Service Error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex.status() == 404) {
            message = "Flight Service could not find the requested data.";
            status = HttpStatus.NOT_FOUND;
        } else if (ex.status() == 503) {
            message = "Flight Service is currently unavailable.";
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else if (ex.status() > 0) {
            status = HttpStatus.valueOf(ex.status());
        }
        
        return buildResponse(status, "Dependency Error", message, null);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeErrors(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        
        if (details != null) {
            response.put("details", details);
        }
        
        return new ResponseEntity<>(response, status);
    }
    
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(org.springframework.dao.DuplicateKeyException ex) {
        String msg = "Database Error";
        if (ex.getMessage().contains("unique_passenger_per_flight")) {
            msg = "One or more passengers are already booked on this flight.";
        } else if (ex.getMessage().contains("unique_seat_per_flight")) {
            msg = "Selected seat is already taken.";
        }
        
        return buildResponse(HttpStatus.CONFLICT,"Database Error", "Duplicate Booking", msg);
    }
}