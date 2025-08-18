package com.event.msalearningproject.message.service;


import com.event.msalearningproject.message.dto.MessageType;
import com.event.msalearningproject.message.service.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSenderFactory {
    private final ApplicationContext applicationContext;

    // Factory pattern 구현 - Enum을 메시지 타입에 따라 적절한 MessageSender를 생성
    public MessageSender createMessageSender(MessageType type) {
        return (MessageSender) applicationContext.getBean(type.getMessageSender());
    }
}
