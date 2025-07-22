package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;

public class PushMessage implements MessageSender {

    @Override
    public boolean suport(MessageType type) {
        return type == MessageType.PUSH;
    }

    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {

    }
}
