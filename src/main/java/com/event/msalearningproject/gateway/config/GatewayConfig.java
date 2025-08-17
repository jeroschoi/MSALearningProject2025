package com.event.msalearningproject.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

/**
 * Gateway 전역 설정을 관리하는 설정 클래스
 */
@Configuration
@Slf4j
public class GatewayConfig {

    /**
     * 개발 환경에서 사용할 수동 라우트 설정
     * application-gateway.yml의 설정과 함께 사용
     */
    @Bean
    @Profile("dev")
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring custom routes for development environment");
        
        return builder.routes()
            .route("member-service-dev", r -> r
                .path("/api/v1/members/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway-Source", "gateway-service")
                    .addResponseHeader("X-Gateway-Response", "true"))
                .uri("http://localhost:8081"))
            .route("message-service-dev", r -> r
                .path("/api/v1/messages/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway-Source", "gateway-service")
                    .addResponseHeader("X-Gateway-Response", "true"))
                .uri("http://localhost:8082"))
            .build();
    }

    /**
     * Gateway 전역 필터 설정
     */
    @Bean
    public org.springframework.cloud.gateway.filter.GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            log.debug("Processing request: {} {}", 
                exchange.getRequest().getMethod(), 
                exchange.getRequest().getURI());
            
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.debug("Response status: {}", 
                        exchange.getResponse().getStatusCode());
                }));
        };
    }
}
