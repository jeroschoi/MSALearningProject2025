package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushMessage implements MessageSender {

    @Override
    public boolean suport(MessageType type) {
        return type == MessageType.PUSH;
    }

    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {
        log.info("PushMessage sendMessage called with: {}", messageRequestDto);
        log.info("메시지 전송 완료: {}", messageRequestDto);
    }
}
