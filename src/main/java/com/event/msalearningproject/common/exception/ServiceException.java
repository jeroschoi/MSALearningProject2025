package com.event.msalearningproject.common.exception;

public class ServiceException extends RuntimeException{
    @java.io.Serial
    private static final long serialVersionUID = -703745766939L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
