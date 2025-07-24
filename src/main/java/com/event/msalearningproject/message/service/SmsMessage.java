package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;
import org.springframework.stereotype.Component;

@Component
public class SmsMessage implements MessageSender {

    @Override
    public boolean suport(MessageType type) {
        return type == MessageType.SMS;
    }

    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {

    }
}
