package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;
import org.springframework.stereotype.Component;

@Component
public interface MessageSender {

    boolean suport(MessageType messageType);

    void sendMessage(MessageRequestDto messageRequestDto);
}
