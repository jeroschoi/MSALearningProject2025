package com.event.msalearningproject.message.service.sender;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsMessageSender implements MessageSender {

    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {
        log.info("SmsMessageSender sendMessage called with: {}", messageRequestDto);
        log.info("SMS 메시지 전송 완료: {}", messageRequestDto);
    }
}
