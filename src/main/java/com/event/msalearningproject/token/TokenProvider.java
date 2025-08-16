package com.event.msalearningproject.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${auth.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${auth.jwt.expire-ms}")
    private long jwtExpireMs;

    public String createJwtTokenById(String id){
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + jwtExpireMs);

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(
                        Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecretKey)),
                        SignatureAlgorithm.HS256
                ).compact();

    }

}
