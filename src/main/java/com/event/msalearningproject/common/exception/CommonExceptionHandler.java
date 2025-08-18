package com.event.msalearningproject.common.exception;

import com.event.msalearningproject.common.dto.CommonResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        String errorMessage = e.getMessage();
        return ResponseEntity.status(404).body(
                    CommonResponse.builder()
                            .message(errorMessage)
                            .build()
        );
    }
}
