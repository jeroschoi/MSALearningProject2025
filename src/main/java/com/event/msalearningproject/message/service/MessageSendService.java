package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageSendService {

    private final MessageSenderAdapter adapter;
    private final MessageService messageSendService;

    /**
     * 메시지 저장 및 전송
     */
    @Transactional
    public Boolean sendMessage(MessageRequestDto dto) {
        log.info("메시지 전송 요청 - {}", dto);
        try {
            MessageSender messageSender = adapter.getMessageSender(dto);
            log.info("adapter.getMessageSender(dto) - {}", messageSender);
            messageSendService.saveMessageHistory(dto);
            messageSender.sendMessage(dto);
        } catch (IllegalArgumentException e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            return false;
        }
        log.info("메시지 전송 성공 - {}", dto);
        return true;
    }

}
