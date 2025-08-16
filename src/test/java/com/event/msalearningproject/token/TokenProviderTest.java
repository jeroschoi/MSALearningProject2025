package com.event.msalearningproject.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Token Povider Test")
public class TokenProviderTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("this-is-a-very-strong-secret-key-123456".getBytes());

    @Test
    void createJwtTokenById_shouldContainSubject() {
        TokenProvider provider = new TokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecretKey", SECRET);
        ReflectionTestUtils.setField(provider, "jwtExpireMs", 60000L); // 1분

        String token = provider.createJwtTokenById("member-1");

        // 토큰 파싱 → subject 확인
        String subject = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertEquals("member-1", subject);
    }
}
