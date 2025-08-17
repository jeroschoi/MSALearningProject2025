package com.event.msalearningproject.gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 토큰 검증 및 파싱을 담당하는 클래스
 */
@Component
@Slf4j
public class JwtTokenValidator {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * JWT 서명 키를 생성합니다.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return !claims.getExpiration().before(new java.util.Date());
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     * @throws RuntimeException 토큰 파싱 실패 시
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract user ID from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract user ID from token", e);
        }
    }

    /**
     * JWT 토큰에서 모든 클레임을 추출합니다.
     * 
     * @param token JWT 토큰
     * @return JWT 클레임 객체
     */
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
}
