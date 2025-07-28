package com.event.msalearningproject.member.exception;

public class MemberException extends RuntimeException {
    
    private final MemberErrorCode errorCode;
    
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public MemberException(MemberErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public MemberException(MemberErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    public MemberErrorCode getErrorCode() {
        return errorCode;
    }
} 