package com.event.msalearningproject.auth.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.yml 파일의 'jwt'로 시작하는 설정들을 바인딩하는 클래스입니다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenValidityInSeconds;
    private long refreshTokenValidityInSeconds;

}
