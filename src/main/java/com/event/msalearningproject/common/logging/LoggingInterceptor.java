package com.event.msalearningproject.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * HTTP 요청/응답 로깅 인터셉터
 * 모든 HTTP 요청과 응답을 자동으로 로깅합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private final LoggingService loggingService;
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    // 요청 전 처리
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // TraceId 생성 또는 기존 것 사용
        String traceId = getOrGenerateTraceId(request);
        request.setAttribute("traceId", traceId);
        
        // 시작 시간 저장
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // 요청 내용 로깅 (GET 요청 제외)
        if (!"GET".equals(request.getMethod())) {
            try {
                String requestBody = getRequestBody(request);
                loggingService.logRequest(traceId, request.getMethod(), request.getRequestURI(), requestBody);
            } catch (IOException e) {
                log.warn("Failed to read request body for logging", e);
            }
        } else {
            loggingService.logRequest(traceId, request.getMethod(), request.getRequestURI(), "GET request");
        }
        
        // 계속 진행
        return true;
    }

    // 요청 후 처리
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String traceId = (String) request.getAttribute("traceId");
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long executionTime = startTime != null ? System.currentTimeMillis() - startTime : 0;
        
        if (ex != null) {
            // 에러 로깅
            loggingService.logError(traceId, request.getMethod(), request.getRequestURI(), ex);
        } else {
            // 응답 로깅
            loggingService.logResponse(traceId, request.getMethod(), request.getRequestURI(), 
                    "Status: " + response.getStatus(), executionTime);
        }
        
        // 성능 로깅
        loggingService.logPerformance(traceId, request.getMethod() + " " + request.getRequestURI(), executionTime);
    }

    private String getOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = loggingService.generateTraceId();
        }
        return traceId;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }
} 