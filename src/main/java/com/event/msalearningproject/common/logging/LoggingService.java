package com.event.msalearningproject.common.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 공통 로깅 서비스
 * 요청/응답 로깅, 성능 측정, 에러 로깅을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 요청 로깅
     * 무엇을 요청했는지 로깅
     */
    public void logRequest(String traceId, String method, String uri, Object requestBody) {
        log.info("[REQUEST] TraceId: {}, Method: {}, URI: {}, RequestBody: {}, Timestamp: {}", 
                traceId, method, uri, requestBody, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 응답 로깅
     * 무엇을 응답했는지 로깅
     */
    public void logResponse(String traceId, String method, String uri, Object responseBody, long executionTime) {
        log.info("[RESPONSE] TraceId: {}, Method: {}, URI: {}, ResponseBody: {}, ExecutionTime: {}ms, Timestamp: {}", 
                traceId, method, uri, responseBody, executionTime, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 에러 로깅
     * 에러 내용 로깅
     */
    public void logError(String traceId, String method, String uri, Exception exception) {
        log.error("[ERROR] TraceId: {}, Method: {}, URI: {}, Error: {}, Timestamp: {}", 
                traceId, method, uri, exception.getMessage(), LocalDateTime.now().format(FORMATTER), exception);
    }

    /**
     * 비즈니스 로직 로깅
     * 비즈니스 로직 내용 로깅
     */
    public void logBusinessLogic(String traceId, String service, String method, String message) {
        log.info("[BUSINESS] TraceId: {}, Service: {}, Method: {}, Message: {}, Timestamp: {}", 
                traceId, service, method, message, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 성능 측정 로깅
     * 성능 측정 내용 로깅. 시간
     */
    public void logPerformance(String traceId, String operation, long executionTime) {
        String level = executionTime > 1000 ? "WARN" : "INFO";
        log.atLevel(level.equals("WARN") ? org.slf4j.event.Level.WARN : org.slf4j.event.Level.INFO)
           .log("[PERFORMANCE] TraceId: {}, Operation: {}, ExecutionTime: {}ms, Timestamp: {}", 
                traceId, operation, executionTime, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * Trace ID 생성
     */
    public String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
} 