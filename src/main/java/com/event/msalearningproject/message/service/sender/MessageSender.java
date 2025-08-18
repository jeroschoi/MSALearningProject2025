package com.event.msalearningproject.message.service.sender;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import org.springframework.stereotype.Component;

@Component
public interface MessageSender {

    void sendMessage(MessageRequestDto messageRequestDto);
}
