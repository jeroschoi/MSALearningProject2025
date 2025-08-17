package com.event.msalearningproject.gateway.filter;

import com.event.msalearningproject.gateway.exception.GatewayException;
import com.event.msalearningproject.gateway.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT 토큰 인증을 처리하는 Gateway 필터
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        super(Config.class);
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerWebExchange modifiedExchange = validateToken(exchange);
            return chain.filter(modifiedExchange);
        };
    }

    /**
     * JWT 토큰을 검증하고 사용자 정보를 헤더에 추가합니다.
     * 
     * @param exchange ServerWebExchange 객체
     * @return 수정된 ServerWebExchange 객체
     * @throws GatewayException 토큰 검증 실패 시
     */
    private ServerWebExchange validateToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization header missing or invalid format");
            throw new GatewayException("Unauthorized: Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        
        try {
            if (!jwtTokenValidator.validateToken(token)) {
                log.warn("Invalid JWT token");
                throw new GatewayException("Unauthorized: Invalid token", HttpStatus.UNAUTHORIZED);
            }
            
            // 토큰 검증 성공 시 사용자 정보를 헤더에 추가
            String userId = jwtTokenValidator.getUserIdFromToken(token);
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build())
                .build();
            
            log.info("JWT token validated successfully for user: {}", userId);
            return modifiedExchange;
            
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            throw new GatewayException("Unauthorized: Token validation failed", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 필터 설정을 위한 configuration class
     */
    public static class Config {
        // 필터 설정을 위한 configuration class
    }
}
