package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.dto.MultiMessageResponse;
import com.event.msalearningproject.message.exception.MessageErrorCode;
import com.event.msalearningproject.message.exception.MessageException;
import com.event.msalearningproject.message.service.sender.MessageSender;
import io.netty.handler.codec.MessageAggregationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageSendService {

    private final MessageSenderFactory messageSenderFactory;
    private final MessageService messageSendService;

    /**
     * 메시지 저장 및 전송
     */
    @Transactional
    public Boolean sendMessage(MessageRequestDto dto) {
        log.info("메시지 전송 요청 - {}", dto);
        try {
            MessageSender messageSender = messageSenderFactory.createMessageSender(dto.getMessageType());
            log.info("Sender Type Check  - {}", messageSender.getClass().getName());
            messageSender.sendMessage(dto);
            //TODO: 메시지 전송 Event 발행으로 변경 처리 필요
            messageSendService.saveMessageHistory(dto);
        } catch (Exception e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAIL, e.getMessage());
        }
        log.info("메시지 전송 성공 - {}", dto);
        return true;
    }

    @Transactional
    public MultiMessageResponse sendMultiMessage(List<MessageRequestDto> dtoList) {

        List<MessageRequestDto> failedList = new ArrayList<MessageRequestDto>();

        dtoList.forEach(dto -> {
            try {
                sendMessage(dto);
            } catch (Exception e) {
                failedList.add(dto);
            }
        });

        return MultiMessageResponse.builder()
                .totalCount(Integer.valueOf(dtoList.size()).longValue())
                .sentCount(Integer.valueOf(dtoList.size() - failedList.size()).longValue())
                .failedCount(Integer.valueOf(failedList.size()).longValue())
                .failedMessages(failedList)
                .build();
    }
}
