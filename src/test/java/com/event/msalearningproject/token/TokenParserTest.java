package com.event.msalearningproject.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenParserTest {
    private static final String SECRET = Base64.getEncoder()
            .encodeToString("this-is-a-very-strong-secret-key-123456".getBytes());

    @Test
    void parseTokenAndGetMemberId_shouldReturnSubject() {

        String token = Jwts.builder()
                .setSubject("member-2")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(
                        Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET)),
                        SignatureAlgorithm.HS256
                ).compact();

        TokenParser parser = new TokenParser();
        ReflectionTestUtils.setField(parser, "jwtSecretKey", SECRET);

        // when
        String memberId = parser.parseTokenAndGetMemberId(token);

        // then
        assertEquals("member-2", memberId);
    }
}
