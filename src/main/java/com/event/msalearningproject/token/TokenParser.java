package com.event.msalearningproject.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenParser {

    @Value("${auth.jwt.secret-key}")
    private String jwtSecretKey;

    public String parseTokenAndGetMemberId(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

}
