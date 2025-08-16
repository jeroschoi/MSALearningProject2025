package com.event.msalearningproject.auth.service;

import com.event.msalearningproject.auth.dto.LoginRequestDto;
import com.event.msalearningproject.auth.dto.TokenReissueRequestDto;
import com.event.msalearningproject.auth.dto.TokenResponseDto;
import com.event.msalearningproject.auth.util.JwtUtil;
import com.event.msalearningproject.member.exception.MemberErrorCode;
import com.event.msalearningproject.member.exception.MemberException;
import com.event.msalearningproject.member.repository.MemberRepository;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.event.msalearningproject.auth.AuthConstants.REFRESH_TOKEN_PREFIX;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        //사용자 ID로 DB에서 회원 정보 조회
        MemberEntity member = memberRepository.findByUserId(loginRequestDto.getUserId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        //비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        //탈퇴한 회원인지 확인
        if (!member.isActive()) {
            throw new MemberException(MemberErrorCode.ALREADY_EXITED);
        }

        //Access/ Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(member.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getUserId());

        //생성된 토큰을 Redis에 저장 (Key: userId, Value: accessToken)
        //로그아웃 처리 등을 위해 Redis에 토큰을 저장해둘 수 있습니다.
        long tokenValidityInSeconds = jwtUtil.getAccessTokenValidityInSeconds();
        redisTemplate.opsForValue().set(
                "RT:" + member.getUserId(),
                refreshToken,
                jwtUtil.getRefreshTokenValidityInSeconds(), // Refresh Token 만료 시간
                TimeUnit.SECONDS
        );

        //생성된 토큰을 DTO에 담아 반환
        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponseDto reissue(TokenReissueRequestDto reissueRequestDto) {
        String refreshToken = reissueRequestDto.getRefreshToken();

        // 1. Refresh Token 유효성 검증 (만료 여부)
        if (jwtUtil.isTokenExpired(refreshToken)) {
            // MemberErrorCode에 INVALID_TOKEN 같은 Enum 값을 추가해서 사용하면 더 좋습니다.
            throw new MemberException(MemberErrorCode.INTERNAL_SERVER_ERROR, "만료된 리프레시 토큰입니다.");
        }

        // 2. 토큰에서 사용자 ID 추출
        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        String redisRefreshToken = redisTemplate.opsForValue().get("RT:" + userId);
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new MemberException(MemberErrorCode.INTERNAL_SERVER_ERROR, "유효하지 않은 리프레시 토큰입니다.");
        }

        // 4. 새로운 Access Token 및 Refresh Token 생성 (보안을 위해 Refresh Token도 함께 재발급)
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        // 5. Redis의 Refresh Token 업데이트
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                newRefreshToken,
                jwtUtil.getRefreshTokenValidityInSeconds(),
                TimeUnit.SECONDS
        );

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
