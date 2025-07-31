package com.event.msalearningproject.config.webclient.exception;

public class ExternalClientException extends ExternalServiceException {

    public ExternalClientException(String url, int status, String body) {
        super(String.format("Client error [%d] on [%s]: %s", status, url, body));
    }
}
