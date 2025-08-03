package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageSenderAdapter {

    private final List<MessageSender> messages;

    public MessageSender getMessageSender(MessageRequestDto dto) {
        for (MessageSender sender : messages) {
            if(sender.suport(dto.getMessageType())){
                return sender;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 메시지 타입입니다: " + dto.getMessageType());
    }
}
