package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.repository.entity.MessageHistory;
import com.event.msalearningproject.message.dto.MessageType;
import com.event.msalearningproject.message.service.sender.MessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    MessageService messageService; // 메시지 이력 저장 Mock 추가

    @Mock
    ApplicationContext applicationContext;

    @Mock
    MessageSender messageSender;

    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessage_success() {
        // given
        MessageRequestDto dto = MessageRequestDto.builder()
                .messageType(MessageType.PUSH)
                .memberId("memberId")
                .content("Test message content")
                .phoneNumber("010-1234-5678")
                .build();

        MessageHistory history = MessageHistory.builder()
                .id(1L)
                .memberId(dto.getMemberId())
                .phoneNumber(dto.getPhoneNumber())
                .messageType(dto.getMessageType())
                .content(dto.getContent())
                .sentAt(LocalDateTime.now())
                .visible(true)
                .build();

        when(applicationContext.getBean(dto.getMessageType().getMessageSender())).thenReturn(messageSender);
        when(messageService.saveMessageHistory(dto)).thenReturn(history);
        doNothing().when(messageSender).sendMessage(dto);

        MessageSenderFactory factory = new MessageSenderFactory(applicationContext);

        // when
        boolean result = new MessageSendService(factory , messageService ).sendMessage(dto);

        // then
        assertTrue(result);
        verify(messageService).saveMessageHistory(dto);
        verify(messageSender).sendMessage(dto);
    }

    @DisplayName("MessageSenderFactory가 올바른 Bean을 반환해야 한다")
    @Test
    void should_return_correct_message_sender_bean() {
        MessageType type = MessageType.PUSH;
        when(applicationContext.getBean(type.getMessageSender())).thenReturn(messageSender);

        MessageSenderFactory factory = new MessageSenderFactory(applicationContext);
        MessageSender sender = factory.createMessageSender(type);

        assertNotNull(sender);
        assertEquals(messageSender, sender);
    }
}