package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;

public interface MessageSender {

    boolean suport(MessageType messageType);

    void sendMessage(MessageRequestDto messageRequestDto);
}
