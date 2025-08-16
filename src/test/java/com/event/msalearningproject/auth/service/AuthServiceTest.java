package com.event.msalearningproject.auth.service;

import com.event.msalearningproject.auth.dto.TokenReissueRequestDto;
import com.event.msalearningproject.auth.dto.TokenResponseDto;
import com.event.msalearningproject.auth.util.JwtUtil;
import com.event.msalearningproject.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations; // RedisTemplate의 동작을 Mocking하기 위해 필요

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_Success() {
        // given
        String oldRefreshToken = "old-refresh-token";
        String userId = "testuser";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        TokenReissueRequestDto requestDto = new TokenReissueRequestDto();
        // Reflection을 사용하거나, Setter를 추가하여 테스트 값을 설정합니다.
        try {
            var field = requestDto.getClass().getDeclaredField("refreshToken");
            field.setAccessible(true);
            field.set(requestDto, oldRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(jwtUtil.isTokenExpired(oldRefreshToken)).thenReturn(false);
        when(jwtUtil.getUserIdFromToken(oldRefreshToken)).thenReturn(userId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations); // opsForValue() 호출 시 Mock 객체 반환
        when(valueOperations.get("RT:" + userId)).thenReturn(oldRefreshToken);
        when(jwtUtil.generateAccessToken(userId)).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(userId)).thenReturn(newRefreshToken);

        // when
        TokenResponseDto result = authService.reissue(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 리프레시 토큰")
    void reissue_Fail_ExpiredToken() {
        // given
        String expiredToken = "expired-refresh-token";
        TokenReissueRequestDto requestDto = new TokenReissueRequestDto();
        try {
            var field = requestDto.getClass().getDeclaredField("refreshToken");
            field.setAccessible(true);
            field.set(requestDto, expiredToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(jwtUtil.isTokenExpired(expiredToken)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.reissue(requestDto))
                .isInstanceOf(MemberException.class)
                .hasMessage("만료된 리프레시 토큰입니다.");
    }

    @Test
    @DisplayName("토큰 재발급 실패 - Redis에 저장된 토큰과 불일치")
    void reissue_Fail_TokenMismatch() {
        // given
        String requestToken = "request-refresh-token";
        String storedToken = "stored-refresh-token";
        String userId = "testuser";
        TokenReissueRequestDto requestDto = new TokenReissueRequestDto();
        try {
            var field = requestDto.getClass().getDeclaredField("refreshToken");
            field.setAccessible(true);
            field.set(requestDto, requestToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(jwtUtil.isTokenExpired(requestToken)).thenReturn(false);
        when(jwtUtil.getUserIdFromToken(requestToken)).thenReturn(userId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("RT:" + userId)).thenReturn(storedToken);

        // when & then
        assertThatThrownBy(() -> authService.reissue(requestDto))
                .isInstanceOf(MemberException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }
}
