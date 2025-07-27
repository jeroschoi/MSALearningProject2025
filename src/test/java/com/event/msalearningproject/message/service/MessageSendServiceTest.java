package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
import com.event.msalearningproject.message.entity.MessageType;
import com.event.msalearningproject.message.repository.MessageRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    MessageService messageService; // 메시지 이력 저장 Mock 추가

    @Mock
    MessageSenderAdapter messageSenderAdapter;

    @Mock
    MessageSender messageSender;

    @InjectMocks
    MessageSendService messageSendService;

    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessage_success() {
        // given
        MessageRequestDto dto = new MessageRequestDto();
        dto.setMemberId("memberId");
        dto.setPhoneNumber("010-1234-5678");
        dto.setContent("Test message content");
        dto.setMessageType(MessageType.PUSH);

        MessageHistory history = MessageHistory.builder()
                .id(1L)
                .memberId(dto.getMemberId())
                .phoneNumber(dto.getPhoneNumber())
                .messageType(dto.getMessageType())
                .content(dto.getContent())
                .sentAt(LocalDateTime.now())
                .visible(true)
                .build();

        when(messageSenderAdapter.getMessageSender(dto)).thenReturn(messageSender);
        when(messageService.saveMessageHistory(dto)).thenReturn(history);
        doNothing().when(messageSender).sendMessage(dto);

        // when
        boolean result = messageSendService.sendMessage(dto);

        // then
        assertTrue(result);
        verify(messageSenderAdapter).getMessageSender(dto);
        verify(messageService).saveMessageHistory(dto);
        verify(messageSender).sendMessage(dto);
    }

    @Test
    @DisplayName("PUSH 타입일 때 PushMessage 전송이 되어야 한다.")
    void adapter_should_return_push_sender() {
        // given
        MessageRequestDto dto = new MessageRequestDto();
        dto.setMessageType(MessageType.PUSH);

        // 실제 전략 객체 리스트 구성
        MessageSender push = new PushMessage();
        MessageSenderAdapter adapter = new MessageSenderAdapter(List.of(push));

        // when
        MessageSender selected = adapter.getMessageSender(dto);

        // then
        assertNotNull(selected);
        assertEquals(PushMessage.class, selected.getClass());
    }
}