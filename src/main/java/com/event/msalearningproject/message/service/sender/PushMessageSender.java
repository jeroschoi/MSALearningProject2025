package com.event.msalearningproject.message.service.sender;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushMessageSender implements MessageSender {

    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {
        log.info("PushMessage sendMessage called with: {}", messageRequestDto);
        log.info("PUSH 메시지 전송 완료: {}", messageRequestDto);
    }
}
