package com.event.msalearningproject.config.webclient.exception;

public class ExternalServerException extends ExternalServiceException {

    public ExternalServerException(String url, int status, String body) {
        super(String.format("Server error [%d] on [%s]: %s", status, url, body));
    }
}
