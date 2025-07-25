package com.event.msalearningproject.member.mapper;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.repository.entity.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberMapper 테스트")
class MemberMapperTest {

    private MemberMapper memberMapper;

    @BeforeEach
    void setUp() {
        memberMapper = Mappers.getMapper(MemberMapper.class);
    }

    @Test
    @DisplayName("MemberJoinRequest를 MemberEntity로 변환")
    void toEntity_FromJoinRequest() {
        // given
        MemberJoinRequest request = MemberJoinRequest.builder()
                .userId("testuser")
                .password("password123")
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .build();

        // when
        MemberEntity entity = memberMapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getUserId()).isEqualTo("testuser");
        assertThat(entity.getPassword()).isEqualTo("password123");
        assertThat(entity.getName()).isEqualTo("춘봉");
        assertThat(entity.getEmail()).isEqualTo("testuser@naver.com");
        assertThat(entity.getContact()).isEqualTo("010-1234-5678");
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    @DisplayName("MemberEntity를 MemberResponse로 변환")
    void toResponse_FromEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        MemberEntity entity = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .password("encodedPassword")
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .messageType(MessageType.SMS)
                .active(true)
                .joinDate(now)
                .exitDate(null)
                .build();

        // when
        MemberResponse response = memberMapper.toResponse(entity);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo("testuser");
        assertThat(response.getName()).isEqualTo("춘봉");
        assertThat(response.getEmail()).isEqualTo("testuser@naver.com");
        assertThat(response.getContact()).isEqualTo("010-1234-5678");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getJoinDate()).isEqualTo(now);
        assertThat(response.getExitDate()).isNull();
        // password는 응답에 포함되지 않아야 함 (DTO에 password 필드가 없으므로 자동으로 제외됨)
    }

    @Test
    @DisplayName("MemberEntity를 MemberResponse로 변환 - 탈퇴한 회원")
    void toResponse_FromInactiveEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exitDate = now.plusDays(30);
        MemberEntity entity = MemberEntity.builder()
                .id(1L)
                .userId("testuser")
                .password("encodedPassword")
                .name("춘봉")
                .email("testuser@naver.com")
                .contact("010-1234-5678")
                .messageType(MessageType.SMS)
                .active(false)
                .joinDate(now)
                .exitDate(exitDate)
                .build();

        // when
        MemberResponse response = memberMapper.toResponse(entity);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isActive()).isFalse();
        assertThat(response.getExitDate()).isEqualTo(exitDate);
    }

    @Test
    @DisplayName("MemberEntity 리스트를 MemberResponse 리스트로 변환")
    void toResponseList_FromEntityList() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<MemberEntity> entities = List.of(
                MemberEntity.builder()
                        .id(1L)
                        .userId("user1")
                        .name("춘봉")
                        .email("user1@naver.com")
                        .contact("010-1111-1111")
                        .messageType(MessageType.SMS)
                        .active(true)
                        .joinDate(now)
                        .build(),
                MemberEntity.builder()
                        .id(2L)
                        .userId("user2")
                        .name("첨지")
                        .email("user2@naver.com")
                        .contact("010-2222-2222")
                        .messageType(MessageType.EMAIL)
                        .active(true)
                        .joinDate(now)
                        .build()
        );

        // when
        List<MemberResponse> responses = memberMapper.toResponseList(entities);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUserId()).isEqualTo("user1");
        assertThat(responses.get(0).getName()).isEqualTo("춘봉");
        assertThat(responses.get(1).getUserId()).isEqualTo("user2");
        assertThat(responses.get(1).getName()).isEqualTo("첨지");
    }

    @Test
    @DisplayName("활성 MemberEntity 리스트를 MemberResponse 리스트로 변환")
    void toActiveResponseList_FromActiveEntityList() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<MemberEntity> entities = List.of(
                MemberEntity.builder()
                        .id(1L)
                        .userId("user1")
                        .name("춘봉")
                        .email("user1@naver.com")
                        .contact("010-1111-1111")
                        .messageType(MessageType.SMS)
                        .active(true)
                        .joinDate(now)
                        .build(),
                MemberEntity.builder()
                        .id(2L)
                        .userId("user2")
                        .name("나비")
                        .email("user2@naver.com")
                        .contact("010-2222-2222")
                        .messageType(MessageType.EMAIL)
                        .active(false) // 비활성 회원
                        .joinDate(now)
                        .build()
        );

        // when
        List<MemberResponse> responses = memberMapper.toActiveResponseList(entities);

        // then
        assertThat(responses).hasSize(2);
        // 모든 회원이 포함되어야 함 (필터링은 Repository에서 처리)
        assertThat(responses.get(0).getUserId()).isEqualTo("user1");
        assertThat(responses.get(1).getUserId()).isEqualTo("user2");
    }

    @Test
    @DisplayName("빈 리스트 변환")
    void toResponseList_EmptyList() {
        // given
        List<MemberEntity> entities = List.of();

        // when
        List<MemberResponse> responses = memberMapper.toResponseList(entities);

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("null 값 처리")
    void toResponse_FromNullEntity() {
        // when
        MemberResponse response = memberMapper.toResponse(null);

        // then
        assertThat(response).isNull();
    }
} 