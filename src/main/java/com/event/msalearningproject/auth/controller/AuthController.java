package com.event.msalearningproject.auth.controller;

import com.event.msalearningproject.auth.dto.LoginRequestDto;
import com.event.msalearningproject.auth.dto.TokenReissueRequestDto;
import com.event.msalearningproject.auth.dto.TokenResponseDto;
import com.event.msalearningproject.auth.service.AuthService;
import com.event.msalearningproject.member.dto.MemberCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msa/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 ID와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    public ResponseEntity<MemberCommonResponse<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponse = authService.login(loginRequestDto);
        return ResponseEntity.ok(MemberCommonResponse.success(tokenResponse));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    public ResponseEntity<MemberCommonResponse<TokenResponseDto>> reissue(@RequestBody TokenReissueRequestDto reissueRequestDto) {
        TokenResponseDto tokenResponse = authService.reissue(reissueRequestDto);
        return ResponseEntity.ok(MemberCommonResponse.success(tokenResponse));
    }
}
