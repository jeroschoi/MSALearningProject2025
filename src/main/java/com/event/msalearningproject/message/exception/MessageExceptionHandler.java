package com.event.msalearningproject.message.exception;

import com.event.msalearningproject.message.dto.GlobalErrorReponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Hidden // ControllerAdvice 와 Swagger UI 가 충돌하는 경우가 있어 숨김 처리
public class MessageExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GlobalErrorReponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        String errorMessage = e.getMessage();
        GlobalErrorReponseDto responseDto = new GlobalErrorReponseDto();
        responseDto.setMessaget(errorMessage);
        return ResponseEntity.status(404).body(responseDto);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<GlobalErrorReponseDto> handleDataAccessException(DataAccessException e) {
        String errorMessage = "Database access error: " + e.getMessage();
        GlobalErrorReponseDto responseDto = new GlobalErrorReponseDto();
        responseDto.setMessaget(errorMessage);
        return ResponseEntity.status(500).body(responseDto);
    }
}
