package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
import com.event.msalearningproject.message.entity.MessageType;
import com.event.msalearningproject.message.repository.MessageRespository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class) // Mockito 객체를 생성하기 위한 애노테이션
class MessageServiceTest {

    @Mock  // Mock 객체를 생성하기 위한 애노테이션
    MessageRespository repository;

    @InjectMocks // Mock 객체를 주입 받기 위한 애노테이션
    MessageService messageService;

    @Test
    @DisplayName("휴대폰 번호로 메시지 이력 조회 성공 테스트")
    void getMessagePhoneNumberSuccessTest() {
        String phoneNumber = "010-1234-5678";

        // given
        MessageRequestDto dto = new MessageRequestDto();
        dto.setMemberId("memberId");
        dto.setPhoneNumber(phoneNumber);
        dto.setContent("Test message content");
        dto.setMessageType(MessageType.SMS);


        MessageHistory savedEntity = MessageHistory.builder()
                .id(1L)
                .memberId(dto.getMemberId())
                .phoneNumber(dto.getPhoneNumber())
                .messageType(dto.getMessageType())
                .content(dto.getContent())
                .sentAt(LocalDateTime.now())
                .visible(true)
                .build();

        // when
        Mockito.when(repository.findByPhoneNumberOrderBySentAtDesc(phoneNumber))
                .thenReturn(Collections.singletonList(savedEntity));

        List<MessageHistory> messageHistoryList = messageService.getMessagePhoneNumber(phoneNumber);

        // then
        assertThat(messageHistoryList).isNotNull();
    }


    @Test
    @DisplayName("휴대폰 번호로 메시지 이력 조회 실패 테스트")
    void getMessagePhoneNumberFailTest() {
        // given
        String phoneNumber = "010-1234-5678";

        // when
        Mockito.when(repository.findByPhoneNumberOrderBySentAtDesc(phoneNumber))
                .thenReturn(Collections.emptyList());
        // then
        assertThatThrownBy(() -> {
            messageService.getMessagePhoneNumber(phoneNumber);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No message history found for phone number: " + phoneNumber);
    }

    @Test
    @DisplayName("회원 ID로 메시지 이력 조회 성공 테스트")
    void getMessageIdSuccessTest() {
        // given
        String memberId = "member123";

        MessageHistory messageHistory = MessageHistory.builder()
                .id(1L)
                .memberId(memberId)
                .phoneNumber("010-1234-5678")
                .messageType(MessageType.SMS)
                .content("Test message content")
                .sentAt(LocalDateTime.now())
                .visible(true)
                .build();

        // when
        Mockito.when(repository.findByMemberIdOrderBySentAtDesc(memberId))
                .thenReturn(Collections.singletonList(messageHistory));

        List<MessageHistory> result = messageService.getMessageMemberId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(0).getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("회원 ID로 메시지 이력 조회 실패 테스트")
    void getMessageIdFailTest() {
        // given
        String memberId = "member123";

        // when
        Mockito.when(repository.findByMemberIdOrderBySentAtDesc(memberId))
                .thenReturn(Collections.emptyList());

        // then
        assertThatThrownBy(() -> {
            messageService.getMessageMemberId(memberId);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No message history found for member id: " + memberId);
    }

}