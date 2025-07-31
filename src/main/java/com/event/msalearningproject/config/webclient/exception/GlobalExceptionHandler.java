package com.event.msalearningproject.config.webclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalClientException.class)
    public ResponseEntity<Map<String, Object>> handleClientError(ExternalClientException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Client Error", ex.getMessage());
    }

    @ExceptionHandler(ExternalServerException.class)
    public ResponseEntity<Map<String, Object>> handleServerError(ExternalServerException ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", ex.getMessage());
    }

    @ExceptionHandler(ExternalTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(ExternalTimeoutException ex) {
        return buildError(HttpStatus.GATEWAY_TIMEOUT, "Timeout", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleGenericExternal(ExternalServiceException ex) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "External Error", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String type, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
