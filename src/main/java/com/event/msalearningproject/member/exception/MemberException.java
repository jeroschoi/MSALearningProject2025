package com.event.msalearningproject.member.exception;

import com.event.msalearningproject.common.exception.ServiceException;
import lombok.Getter;

@Getter
public class MemberException extends ServiceException {

    private final MemberErrorCode errorCode;
    private final String message;

    public MemberException(MemberErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}