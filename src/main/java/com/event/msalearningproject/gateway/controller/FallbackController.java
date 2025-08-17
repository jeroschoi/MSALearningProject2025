package com.event.msalearningproject.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 서비스 장애 시 Fallback 응답을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    /**
     * Member 서비스 장애 시 Fallback 응답
     */
    @GetMapping("/member")
    public Mono<ResponseEntity<Map<String, Object>>> memberFallback() {
        log.warn("Member service is unavailable, using fallback");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", "Member service is temporarily unavailable");
        response.put("status", 503);
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "member-service");
        
        return Mono.just(ResponseEntity.status(503).body(response));
    }

    /**
     * Message 서비스 장애 시 Fallback 응답
     */
    @GetMapping("/message")
    public Mono<ResponseEntity<Map<String, Object>>> messageFallback() {
        log.warn("Message service is unavailable, using fallback");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", "Message service is temporarily unavailable");
        response.put("status", 503);
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "message-service");
        
        return Mono.just(ResponseEntity.status(503).body(response));
    }

    /**
     * 일반적인 서비스 장애 시 Fallback 응답
     */
    @GetMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        log.warn("Service is unavailable, using default fallback");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", "Service is temporarily unavailable");
        response.put("status", 503);
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "unknown-service");
        
        return Mono.just(ResponseEntity.status(503).body(response));
    }
}
