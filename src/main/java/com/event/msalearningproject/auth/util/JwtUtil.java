package com.event.msalearningproject.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key secretKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public JwtUtil(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInSeconds = jwtProperties.getAccessTokenValidityInSeconds();
        this.refreshTokenValidityInSeconds = jwtProperties.getRefreshTokenValidityInSeconds();
    }

    /**
     * 사용자 ID를 기반으로 Access Token을 생성합니다.
     * @param userId 사용자 ID
     * @return 생성된 JWT 문자열
     */
    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.refreshTokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     * @param token JWT 문자열
     * @return 추출된 사용자 ID
     */
    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * @param token JWT 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰이 만료되었는지 확인합니다. (validateToken과 달리 만료 여부만 체크)
     * @param token JWT 문자열
     * @return 만료되었으면 true
     */
    public boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            // 만료 외 다른 예외도 만료된 것으로 처리하여 접근을 막습니다.
            return true;
        }
    }

    /**
     * 토큰에서 Claims 정보를 추출합니다.
     * @param token JWT 문자열
     * @return 토큰에 담긴 Claims 정보
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 설정된 Access Token 유효 시간을 반환합니다.
     * @return 토큰 유효 시간 (초)
     */
    public long getAccessTokenValidityInSeconds() {
        return this.accessTokenValidityInSeconds;
    }

    /**
     * 설정된 Refresh Token 유효 시간을 반환합니다.
     * @return 토큰 유효 시간 (초)
     */
    public long getRefreshTokenValidityInSeconds() { // 👈 메소드 추가
        return this.refreshTokenValidityInSeconds;
    }
}