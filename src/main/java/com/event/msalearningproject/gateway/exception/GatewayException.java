package com.event.msalearningproject.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Gateway에서 발생하는 예외를 처리하기 위한 커스텀 예외 클래스
 */
@Getter
public class GatewayException extends RuntimeException {

    private final HttpStatus status;

    public GatewayException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public GatewayException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
