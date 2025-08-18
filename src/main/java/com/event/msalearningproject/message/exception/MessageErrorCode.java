package com.event.msalearningproject.message.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum MessageErrorCode {

    // 메시지 전송 관련 에러
    MESSAGE_SEND_FAIL("M001", "Message sending failed"),;

    private final String code;
    private final String message;
}
