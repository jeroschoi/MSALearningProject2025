package com.event.msalearningproject.message.dto;

import lombok.Getter;

@Getter
public enum MessageType {
    PUSH("PUSH","pushMessage"),     // 앱 푸시
    KAKAO("KAKAO","KakaoMessage"),     // 앱 푸시
    SMS("SMS","pushMessage"),     // 앱 푸시
    EMAIL("EMAIL","pushMessage")     // 앱 푸시
    ;

    private final String type; // 메시지 타입 이름
    private final String messageSender; // 메시지 타입 이름

    MessageType(String name, String MessageSender) {
        this.type = name;
        this.messageSender = MessageSender;
    }

//    public static MessageType fromString(String type) {
//        for (MessageType messageType : MessageType.values()) {
//            if (messageType.getType().equalsIgnoreCase(type)) {
//                return messageType;
//            }
//        }
//        // throw new IllegalArgumentException("지원 하지 않는 메시지 타입 입니다: " + type);
//        throw new IllegalArgumentException("Not Support Message Type - : " + type);
//    }
}
