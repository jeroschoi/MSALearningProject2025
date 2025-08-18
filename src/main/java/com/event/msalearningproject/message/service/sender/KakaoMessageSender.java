package com.event.msalearningproject.message.service.sender;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoMessageSender implements MessageSender {
    @Override
    public void sendMessage(MessageRequestDto messageRequestDto) {
        log.info("KakaoMessageSender sendMessage called with: {}", messageRequestDto);
        log.info("KAKAO 메시지 전송 완료: {}", messageRequestDto);
    }
}
