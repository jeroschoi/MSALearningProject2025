package com.event.msalearningproject.config.webclient.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerCustomConfig {

    // Circuit Breaker 설정
    @Bean
    public CircuitBreaker externalServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig
                .custom()
                .failureRateThreshold(50) // 실패 비율 임계값
                .waitDurationInOpenState(Duration.ofSeconds(10)) // OPEN -> HALF_OPEN 전 대기시간
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // 최근 10개 호출 기준
                .minimumNumberOfCalls(5) // 통계를 낼 최소 호출 수
                .permittedNumberOfCallsInHalfOpenState(2)
                .recordExceptions(Throwable.class) // 어떤 예외를 실패로 볼 것인가
                .build();
        return CircuitBreaker.of("externalService", config);
    }
}
