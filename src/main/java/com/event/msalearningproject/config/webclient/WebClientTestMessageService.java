package com.event.msalearningproject.config.webclient;

import com.event.msalearningproject.config.webclient.service.CommonWebClientService;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.service.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientTestMessageService implements MessageSender {

    private final CommonWebClientService webClientService;

    @Override
    public void sendMessage(MessageRequestDto dto) {
        String url = "http://localhost:9099/msa/v1/message/send/single";

        log.info("WebClient 메시지 발송 시작 - DTO: {}", dto);

        try {
            webClientService.postSync(url, dto, Void.class, null);
            log.info("WebClient 메시지 발송 성공");
        } catch (Exception e) {
            log.error("WebClient 메시지 발송 실패", e);
            throw new RuntimeException("메시지 전송 실패", e);
        }
    }
}
