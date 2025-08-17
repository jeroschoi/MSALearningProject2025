package com.event.msalearningproject.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 JWT 토큰을 생성하는 유틸리티 클래스
 */
@Component
@Slf4j
public class JwtTokenGenerator {

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
     * 사용자 ID를 기반으로 JWT 토큰을 생성합니다.
     * 
     * @param userId 사용자 ID
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId) {
        return generateToken(userId, new HashMap<>());
    }

    /**
     * 사용자 ID와 추가 클레임을 기반으로 JWT 토큰을 생성합니다.
     * 
     * @param userId 사용자 ID
     * @param claims 추가 클레임
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String userId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .addClaims(claims)
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * 테스트용 기본 JWT 토큰을 생성합니다.
     * 
     * @return 테스트용 JWT 토큰
     */
    public String generateTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("test", true);
        
        return generateToken("test-user", claims);
    }

    /**
     * 토큰의 만료 시간을 확인합니다.
     * 
     * @param token JWT 토큰
     * @return 토큰이 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
            
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}
