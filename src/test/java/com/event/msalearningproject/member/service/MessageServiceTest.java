package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.repository.MessageHistoryRepository;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.repository.entity.MessageHistoryEntity;
import com.event.msalearningproject.member.repository.entity.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 테스트")
class MessageServiceTest {

    @Mock
    private MessageHistoryRepository messageHistoryRepository;

    @InjectMocks
    private MessageService messageService;

    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        memberEntity = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .messageType(MessageType.SMS)
                .active(true)
                .joinDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("회원가입 메시지 전송 성공")
    void sendJoinMessage_Success() {
        // given
        MessageHistoryEntity expectedHistory = MessageHistoryEntity.builder()
                .member(memberEntity)
                .messageType(MessageType.SMS)
                .to("010-1234-5678")
                .content("회원가입을 축하합니다!")
                .sent(true)
                .build();

        when(messageHistoryRepository.save(any(MessageHistoryEntity.class))).thenReturn(expectedHistory);

        // when
        messageService.sendJoinMessage(memberEntity);

        // then
        verify(messageHistoryRepository).save(any(MessageHistoryEntity.class));
    }

    @Test
    @DisplayName("회원가입 메시지 전송 실패")
    void sendJoinMessage_Failure() {
        // given
        when(messageHistoryRepository.save(any(MessageHistoryEntity.class)))
                .thenThrow(new RuntimeException("메시지 전송 실패"));

        // when & then
        assertThatThrownBy(() -> messageService.sendJoinMessage(memberEntity))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("메시지 전송 실패");

        verify(messageHistoryRepository, times(1)).save(any(MessageHistoryEntity.class));
    }

    @Test
    @DisplayName("회원탈퇴 메시지 전송 성공")
    void sendExitMessage_Success() {
        // given
        MessageHistoryEntity expectedHistory = MessageHistoryEntity.builder()
                .member(memberEntity)
                .messageType(MessageType.SMS)
                .to("010-1234-5678")
                .content("회원탈퇴가 완료되었습니다.")
                .sent(true)
                .build();

        when(messageHistoryRepository.save(any(MessageHistoryEntity.class))).thenReturn(expectedHistory);

        // when
        messageService.sendExitMessage(memberEntity);

        // then
        verify(messageHistoryRepository).save(any(MessageHistoryEntity.class));
    }

    @Test
    @DisplayName("회원탈퇴 메시지 전송 실패")
    void sendExitMessage_Failure() {
        // given
        when(messageHistoryRepository.save(any(MessageHistoryEntity.class)))
                .thenThrow(new RuntimeException("메시지 전송 실패"));

        // when & then
        assertThatThrownBy(() -> messageService.sendExitMessage(memberEntity))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("메시지 전송 실패");

        verify(messageHistoryRepository, times(1)).save(any(MessageHistoryEntity.class));
    }

    @Test
    @DisplayName("SMS 타입 회원의 메시지 전송")
    void sendMessage_SMS_Type() {
        // given
        MemberEntity smsMember = MemberEntity.builder()
                .contact("010-1234-5678")
                .email("나비@naver.com")
                .messageType(MessageType.SMS)
                .build();

        when(messageHistoryRepository.save(any(MessageHistoryEntity.class)))
                .thenReturn(MessageHistoryEntity.builder().build());

        // when
        messageService.sendJoinMessage(smsMember);

        // then
        verify(messageHistoryRepository).save(any(MessageHistoryEntity.class));
    }

    @Test
    @DisplayName("EMAIL 타입 회원의 메시지 전송")
    void sendMessage_EMAIL_Type() {
        // given
        MemberEntity emailMember = MemberEntity.builder()
                .contact("010-1234-5678")
                .email("첨지@naver.com")
                .messageType(MessageType.EMAIL)
                .build();

        when(messageHistoryRepository.save(any(MessageHistoryEntity.class)))
                .thenReturn(MessageHistoryEntity.builder().build());

        // when
        messageService.sendJoinMessage(emailMember);

        // then
        verify(messageHistoryRepository).save(any(MessageHistoryEntity.class));
    }
} 