package com.event.msalearningproject.config.webclient.exception;

public class ExternalTimeoutException extends ExternalServiceException {

    public ExternalTimeoutException(String url, Throwable cause) {
        super("Timeout occurred while calling " + url, cause);
    }
}
