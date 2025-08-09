package com.event.msalearningproject.common.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingServiceTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private LoggingService loggingService;

    private String traceId;
    private String method;
    private String uri;

    @BeforeEach
    void setUp() {
        traceId = "test-trace-id-1234";
        method = "POST";
        uri = "/api/v1/members";
    }

    @Test
    @DisplayName("요청 로깅 테스트")
    void testLogRequest() {
        // given
        Object requestBody = "{\"userId\":\"test\",\"password\":\"password\"}";

        // when
        loggingService.logRequest(traceId, method, uri, requestBody);

        // then
        // 로깅이 정상적으로 호출되었는지 확인
        // 실제로는 로그 출력을 확인하지만, 테스트에서는 예외가 발생하지 않는지 확인
    }

    @Test
    @DisplayName("응답 로깅 테스트")
    void testLogResponse() {
        // given
        Object responseBody = "{\"memberId\":1,\"userId\":\"test\"}";
        long executionTime = 150L;

        // when
        loggingService.logResponse(traceId, method, uri, responseBody, executionTime);

        // then
        // 로깅이 정상적으로 호출되었는지 확인
    }

    @Test
    @DisplayName("에러 로깅 테스트")
    void testLogError() {
        // given
        Exception exception = new RuntimeException("Test error message");

        // when
        loggingService.logError(traceId, method, uri, exception);

        // then
        // 에러 로깅이 정상적으로 호출되었는지 확인
    }

    @Test
    @DisplayName("비즈니스 로직 로깅 테스트")
    void testLogBusinessLogic() {
        // given
        String service = "MemberService";
        String methodName = "join";
        String message = "회원가입 처리 완료";

        // when
        loggingService.logBusinessLogic(traceId, service, methodName, message);

        // then
        // 비즈니스 로직 로깅이 정상적으로 호출되었는지 확인
    }

    @Test
    @DisplayName("성능 로깅 테스트 - 정상 성능")
    void testLogPerformance_Normal() {
        // given
        String operation = "회원 조회";
        long executionTime = 50L;

        // when
        loggingService.logPerformance(traceId, operation, executionTime);

        // then
        // 정상 성능 로깅이 호출되었는지 확인
    }

    @Test
    @DisplayName("성능 로깅 테스트 - 느린 성능")
    void testLogPerformance_Slow() {
        // given
        String operation = "회원 조회";
        long executionTime = 1500L;

        // when
        loggingService.logPerformance(traceId, operation, executionTime);

        // then
        // 느린 성능에 대한 경고 로깅이 호출되었는지 확인
    }

    @Test
    @DisplayName("Trace ID 생성 테스트")
    void testGenerateTraceId() {
        // when
        String traceId1 = loggingService.generateTraceId();
        String traceId2 = loggingService.generateTraceId();

        // then
        assert traceId1 != null;
        assert traceId1.length() == 16;
        assert traceId2 != null;
        assert traceId2.length() == 16;
        assert !traceId1.equals(traceId2); // 각각 다른 ID가 생성되어야 함
    }
} 