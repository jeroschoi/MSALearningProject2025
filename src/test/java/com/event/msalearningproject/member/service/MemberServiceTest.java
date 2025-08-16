package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.exception.MemberErrorCode;
import com.event.msalearningproject.member.exception.MemberException;
import com.event.msalearningproject.member.mapper.MemberMapper;
import com.event.msalearningproject.member.repository.MemberRepository;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.repository.entity.MessageType;
import com.event.msalearningproject.message.service.MessageSendService;
import com.event.msalearningproject.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageService messageService;

    @Mock
    private MessageSendService messageSendService;

    @InjectMocks
    private MemberService memberService;

    private MemberJoinRequest joinRequest;
    private MemberEntity memberEntity;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        joinRequest = MemberJoinRequest.builder()
                .userId("testuser")
                .password("password123!") // 원본 비밀번호
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .messageType(MessageType.SMS)
                .build();

        memberEntity = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .password("encodedPassword_!@#") // 암호화된 비밀번호
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .messageType(MessageType.SMS)
                .active(true)
                .joinDate(LocalDateTime.now())
                .build();

        memberResponse = MemberResponse.builder()
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
    @DisplayName("회원가입 성공 시 비밀번호가 암호화되어 저장된다")
    void join_Success_ShouldEncodePassword() {
        // given
        // 실제 코드에 맞게 findBy... 메소드를 stubbing하고, 결과로 Optional.empty()를 반환하여 중복이 없음을 설정
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.empty());

        // toEntity가 호출되면 실제 MemberEntity 객체를 반환하도록 설정 (Null 방지)
        when(memberMapper.toEntity(any(MemberJoinRequest.class))).thenReturn(new MemberEntity());
        when(passwordEncoder.encode(joinRequest.getPassword())).thenReturn("encodedPassword_!@#");
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity);
        when(memberMapper.toResponse(any(MemberEntity.class))).thenReturn(memberResponse);

        // when
        MemberResponse result = memberService.join(joinRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("testuser");

        ArgumentCaptor<MemberEntity> captor = ArgumentCaptor.forClass(MemberEntity.class);
        verify(memberRepository).save(captor.capture());
        MemberEntity savedMember = captor.getValue();

        assertThat(savedMember.getPassword()).isEqualTo("encodedPassword_!@#");
        assertThat(savedMember.getPassword()).isNotEqualTo(joinRequest.getPassword());

        verify(messageSendService).sendMessage(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자 ID")
    void join_Fail_DuplicateUserId() {
        // given
        // 실제 코드에 맞게 findByUserId가 Optional<MemberEntity>를 반환하도록 수정
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> memberService.join(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_USER_ID);

        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void exit_Success() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(memberEntity));
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity);

        // when
        memberService.exit("testuser");

        // then
        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository).save(any(MemberEntity.class));
        verify(messageService).visableFalseMessageHistory(memberEntity.getUserId());
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 존재하지 않는 사용자")
    void exit_Fail_MemberNotFound() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.exit("nonexistent"))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);

        verify(memberRepository, never()).save(any(MemberEntity.class));
    }
}
