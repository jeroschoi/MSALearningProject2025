package com.event.msalearningproject.message.exception;

import com.event.msalearningproject.common.exception.ServiceException;
import com.event.msalearningproject.member.exception.MemberErrorCode;

public class MessageException extends ServiceException {

    private final MessageErrorCode errorCode;
    private final String message;

    public MessageException(MessageErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
