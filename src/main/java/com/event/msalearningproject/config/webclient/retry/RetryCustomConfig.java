package com.event.msalearningproject.config.webclient.retry;

import com.event.msalearningproject.config.webclient.exception.ExternalClientException;
import com.event.msalearningproject.config.webclient.exception.ExternalServerException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.Duration;

@Configuration
public class RetryCustomConfig {

    // retry 설정
    @Bean
    public Retry retryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // 최대 3번 (최초 + 2번 재시도)
                .waitDuration(Duration.ofSeconds(2)) // 고정 대기 시간
                .retryExceptions(WebClientRequestException.class, ExternalServerException.class)
                .ignoreExceptions(ExternalClientException.class)
                .build();
        return Retry.of("externalServiceRetry",config);
    }
}
