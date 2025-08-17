package com.event.msalearningproject.gateway.controller;

import com.event.msalearningproject.gateway.util.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final JwtTokenGenerator jwtTokenGenerator;

    /**
     * Gateway 상태 확인
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "gateway-service");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        log.info("Health check requested");
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * 테스트용 JWT 토큰 생성
     */
    @GetMapping("/token")
    public Mono<ResponseEntity<Map<String, Object>>> generateTestToken() {
        String token = jwtTokenGenerator.generateTestToken();
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("message", "Use this token in Authorization header for testing");
        response.put("example", "Authorization: Bearer " + token);
        
        log.info("Test token generated");
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * 특정 사용자 ID로 JWT 토큰 생성
     */
    @GetMapping("/token/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> generateTokenForUser(String userId) {
        String token = jwtTokenGenerator.generateToken(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("userId", userId);
        response.put("message", "Token generated for user: " + userId);
        
        log.info("Token generated for user: {}", userId);
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Gateway 라우팅 정보 확인
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> getRoutes() {
        Map<String, Object> routes = new HashMap<>();
        
        Map<String, Object> memberRoute = new HashMap<>();
        memberRoute.put("path", "/api/v1/members/**");
        memberRoute.put("uri", "http://localhost:8081");
        memberRoute.put("filters", "JwtAuthenticationFilter, StripPrefix, AddRequestHeader");
        
        Map<String, Object> messageRoute = new HashMap<>();
        messageRoute.put("path", "/api/v1/messages/**");
        messageRoute.put("uri", "http://localhost:8082");
        messageRoute.put("filters", "JwtAuthenticationFilter, StripPrefix, AddRequestHeader");
        
        routes.put("member-service", memberRoute);
        routes.put("message-service", messageRoute);
        routes.put("gateway-port", 8080);
        
        log.info("Route information requested");
        return Mono.just(ResponseEntity.ok(routes));
    }
}
