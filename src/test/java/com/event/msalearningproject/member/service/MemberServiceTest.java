package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.exception.MemberErrorCode;
import com.event.msalearningproject.member.exception.MemberException;
import com.event.msalearningproject.member.mapper.MemberMapper;
import com.event.msalearningproject.member.repository.MemberRepository;
import com.event.msalearningproject.member.repository.MessageHistoryRepository;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.repository.entity.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private MessageHistoryRepository messageHistoryRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private MemberJoinRequest joinRequest;
    private MemberEntity memberEntity;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        joinRequest = MemberJoinRequest.builder()
                .userId("testuser")
                .password("password123")
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .build();

        memberEntity = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .password("encodedPassword")
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
    @DisplayName("회원가입 성공")
    void join_Success() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(any(MemberJoinRequest.class))).thenReturn(memberEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity);
        when(memberMapper.toResponse(any(MemberEntity.class))).thenReturn(memberResponse);

        // when
        MemberResponse result = memberService.join(joinRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("testuser");
        assertThat(result.getName()).isEqualTo("춘봉");

        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository).findByEmail("testuser@naver.com");
        verify(memberRepository).findByContact("010-1234-5678");
        verify(memberMapper).toEntity(joinRequest);
        verify(passwordEncoder).encode("password123");
        verify(memberRepository).save(any(MemberEntity.class));
        verify(messageService).sendJoinMessage(any(MemberEntity.class));
        verify(memberMapper).toResponse(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자 ID")
    void join_Fail_DuplicateUserId() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> memberService.join(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_USER_ID);

        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void join_Fail_DuplicateEmail() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> memberService.join(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_EMAIL);

        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository).findByEmail("testuser@naver.com");
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 연락처")
    void join_Fail_DuplicateContact() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> memberService.join(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_CONTACT);

        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository).findByEmail("testuser@naver.com");
        verify(memberRepository).findByContact("010-1234-5678");
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - db 오류")
    void join_Fail_DatabaseError() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(any(MemberJoinRequest.class))).thenReturn(memberEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberEntity.class))).thenThrow(new DataIntegrityViolationException("DB Error"));

        // when & then
        assertThatThrownBy(() -> memberService.join(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DATABASE_ERROR);
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
        verify(messageService).sendExitMessage(any(MemberEntity.class));
        verify(messageHistoryRepository).deleteByMemberId(1L);
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

        verify(memberRepository).findByUserId("nonexistent");
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 이미 탈퇴한 사용자")
    void exit_Fail_AlreadyExited() {
        // given
        MemberEntity inactiveMember = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .active(false)
                .build();
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(inactiveMember));

        // when & then
        assertThatThrownBy(() -> memberService.exit("testuser"))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.ALREADY_EXITED);

        verify(memberRepository).findByUserId("testuser");
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("사용자 ID로 회원 조회 성공")
    void findByUserId_Success() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.of(memberEntity));
        when(memberMapper.toResponse(any(MemberEntity.class))).thenReturn(memberResponse);

        // when
        MemberResponse result = memberService.findByUserId("testuser");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("testuser");

        verify(memberRepository).findByUserId("testuser");
        verify(memberMapper).toResponse(memberEntity);
    }

    @Test
    @DisplayName("사용자 ID로 회원 조회 실패 - 존재하지 않는 사용자")
    void findByUserId_Fail_MemberNotFound() {
        // given
        when(memberRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findByUserId("nonexistent"))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);

        verify(memberRepository).findByUserId("nonexistent");
    }

    @Test
    @DisplayName("연락처로 회원 조회 성공")
    void findByContact_Success() {
        // given
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.of(memberEntity));
        when(memberMapper.toResponse(any(MemberEntity.class))).thenReturn(memberResponse);

        // when
        MemberResponse result = memberService.findByContact("010-1234-5678");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContact()).isEqualTo("010-1234-5678");

        verify(memberRepository).findByContact("010-1234-5678");
        verify(memberMapper).toResponse(memberEntity);
    }

    @Test
    @DisplayName("연락처로 회원 조회 실패 - 존재하지 않는 연락처")
    void findByContact_Fail_MemberNotFound() {
        // given
        when(memberRepository.findByContact(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findByContact("010-9999-9999"))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);

        verify(memberRepository).findByContact("010-9999-9999");
    }

    @Test
    @DisplayName("활성 회원 목록 조회")
    void getAllActiveMembers_Success() {
        // given
        List<MemberEntity> activeMembers = List.of(memberEntity);
        List<MemberResponse> expectedResponses = List.of(memberResponse);

        when(memberRepository.findByActiveTrue()).thenReturn(activeMembers);
        when(memberMapper.toActiveResponseList(activeMembers)).thenReturn(expectedResponses);

        // when
        List<MemberResponse> result = memberService.getAllActiveMembers();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("testuser");

        verify(memberRepository).findByActiveTrue();
        verify(memberMapper).toActiveResponseList(activeMembers);
    }
} 