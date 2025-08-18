package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.dto.MessageHistoryDto;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.repository.MessageRepository;
import com.event.msalearningproject.message.repository.entity.MessageHistory;
import com.event.msalearningproject.message.dto.MessageType;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class) // Mockito 객체를 생성하기 위한 애노테이션
class MessageServiceTest {

    @Mock  // Mock 객체를 생성하기 위한 애노테이션
    MessageRepository repository;

    @InjectMocks // Mock 객체를 주입 받기 위한 애노테이션
    MessageService messageService;

    @Test
    @DisplayName("메시지 이력 저장 성공 테스트")
    void saveMessageHistorySuccessTest() {
        // given
        MessageRequestDto dto = MessageRequestDto.builder()
                .messageType(MessageType.SMS)
                .memberId("memberId")
                .content("Test message content")
                .phoneNumber("010-1234-5678")
                .build();

        MessageHistory messageHistory = MessageHistory.builder()
                .id(1L)
                .memberId(dto.getMemberId())
                .phoneNumber(dto.getPhoneNumber())
                .messageType(dto.getMessageType())
                .content(dto.getContent())
                .sentAt(LocalDateTime.now())
                .visible(true)
                .build();

        // when
        when(repository.save(Mockito.any(MessageHistory.class)))
                .thenReturn(messageHistory);

        // then
        MessageHistoryDto savedEntity = messageService.saveMessageHistory(dto);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getMemberId()).isEqualTo(dto.getMemberId());
    }

    @Test
    @DisplayName("휴대폰 번호로 메시지 이력 조회 성공 테스트")
    void getMessagePhoneNumberSuccessTest() {
        String phoneNumber = "010-1234-5678";

        // given
        MessageRequestDto dto = MessageRequestDto.builder()
                .messageType(MessageType.SMS)
                .memberId("memberId")
                .content("Test message content")
                .phoneNumber("010-1234-5678")
                .build();


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
        when(repository.findByPhoneNumberAndVisibleTrueOrderBySentAtDesc(phoneNumber))
                .thenReturn(Collections.singletonList(savedEntity));

        List<MessageHistoryDto> messageHistoryList = messageService.getMessagePhoneNumber(phoneNumber);

        // then
        assertThat(messageHistoryList).isNotNull();
    }


    @Test
    @DisplayName("휴대폰 번호로 메시지 이력 조회 실패 테스트")
    void getMessagePhoneNumberFailTest() {
        // given
        String phoneNumber = "010-1234-5678";

        // when
        when(repository.findByPhoneNumberAndVisibleTrueOrderBySentAtDesc(phoneNumber))
                .thenReturn(Collections.emptyList());
        // then
//        assertThatThrownBy(() -> {
//            messageService.getMessagePhoneNumber(phoneNumber);
//        }).isInstanceOf(EntityNotFoundException.class)
//          .hasMessageContaining("No message history found for phone number: " + phoneNumber);
        assertThat(messageService.getMessagePhoneNumber(phoneNumber)).isEmpty();
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
        when(repository.findByMemberIdAndVisibleTrueOrderBySentAtDesc(memberId))
                .thenReturn(Collections.singletonList(messageHistory));

        List<MessageHistoryDto> result = messageService.getMessageMemberId(memberId);

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
        when(repository.findByMemberIdAndVisibleTrueOrderBySentAtDesc(memberId))
                .thenReturn(Collections.emptyList());

        // then
        //        assertThatThrownBy(() -> {
        //            messageService.getMessageMemberId(memberId);
        //        }).isInstanceOf(EntityNotFoundException.class)
        //          .hasMessageContaining("No message history found for member id: " + memberId);
        assertThat(messageService.getMessageMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("회원 탈퇴시 Visible 필드 업데이트 성공 테스트")
    void updateVisibleSuccessTest() {
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

        List<MessageHistory> historyList = Collections.singletonList(messageHistory);

        // when
        when(repository.findByMemberIdAndVisibleTrueOrderBySentAtDesc(memberId))
                .thenReturn(historyList);

        // then
        assertThat(messageService.visibleFalseMessageHistory(memberId)).isTrue();
    }

}