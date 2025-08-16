package com.event.msalearningproject.auth.service;

import com.event.msalearningproject.auth.dto.LoginRequestDto;
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
        // 1. 사용자 ID로 DB에서 회원 정보 조회
        MemberEntity member = memberRepository.findByUserId(loginRequestDto.getUserId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            // 보안을 위해 로그인 실패 시 ID가 틀렸는지, 비밀번호가 틀렸는지 명확히 알려주지 않는 것이 좋습니다.
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 탈퇴한 회원인지 확인
        if (!member.isActive()) {
            throw new MemberException(MemberErrorCode.ALREADY_EXITED);
        }

        // 4. Access Token 생성
        String accessToken = jwtUtil.generateToken(member.getUserId());

        // 5. 생성된 토큰을 Redis에 저장 (Key: userId, Value: accessToken)
        //    로그아웃 처리 등을 위해 Redis에 토큰을 저장해둘 수 있습니다.
        long tokenValidityInSeconds = jwtUtil.getAccessTokenValidityInSeconds();
        redisTemplate.opsForValue().set(
                member.getUserId(),
                accessToken,
                tokenValidityInSeconds,
                TimeUnit.SECONDS
        );

        // 6. 생성된 토큰을 DTO에 담아 반환
        return new TokenResponseDto(accessToken);
    }
}
