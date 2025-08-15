package com.event.msalearningproject.auth.filter;

import com.event.msalearningproject.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String jwt = null;

        // "Bearer "로 시작하는 토큰이 있는지 확인합니다.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.getUserIdFromToken(jwt);
            } catch (Exception e) {
                log.warn("JWT 토큰 파싱 중 오류 발생: {}", e.getMessage());
            }
        }

        // 토큰에서 userId를 정상적으로 추출했고, 아직 인증 정보가 없는 경우
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 토큰이 유효한지 확인합니다. (만료 시간 등)
            if (!jwtUtil.isTokenExpired(jwt)) {
                // Spring Security가 이해할 수 있는 인증 객체를 생성합니다.
                UserDetails userDetails = new User(userId, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // 현재 요청에 대한 인증 정보를 설정합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 인증 정보 저장 완료: {}", userId);
            }
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}
