package com.event.msalearningproject.message.exception;

import com.event.msalearningproject.message.dto.GlobalErrorReponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class MessageExceptionHandler {

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
    public ResponseEntity<GlobalErrorReponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        String errorMessage = e.getMessage();
        GlobalErrorReponseDto responseDto = new GlobalErrorReponseDto();
        responseDto.setMessage(errorMessage);
        return ResponseEntity.status(404).body(responseDto);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<GlobalErrorReponseDto> handleDataAccessException(DataAccessException e) {
        String errorMessage = "Database access error: " + e.getMessage();
        GlobalErrorReponseDto responseDto = new GlobalErrorReponseDto();
        responseDto.setMessage(errorMessage);
        return ResponseEntity.status(500).body(responseDto);
    }

}
