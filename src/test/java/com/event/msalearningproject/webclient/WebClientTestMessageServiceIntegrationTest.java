package com.event.msalearningproject.webclient;

import com.event.msalearningproject.config.webclient.exception.ExternalClientException;
import com.event.msalearningproject.config.webclient.exception.ExternalServerException;
import com.event.msalearningproject.config.webclient.exception.ExternalTimeoutException;
import com.event.msalearningproject.config.webclient.service.CommonWebClientServiceImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class WebClientTestMessageServiceIntegrationTest {

    private static MockWebServer mockWebServer;
    private CommonWebClientServiceImpl service;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        Retry retry = Retry.ofDefaults("test");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("test");
        service = new CommonWebClientServiceImpl(webClient, retry, circuitBreaker);
    }


    @Test
    void getSync_shouldReturnResponseBody() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("pong")
                .addHeader("Content-Type", "application/json"));

        // when
        String response = service.getSync("/ping", String.class, null);

        // then
        assertEquals("pong", response);
    }

    @Test
    void getSync_shouldThrowClientException() {
        // given
        // .timeout 제거된 상태에서 수행
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("잘못된 요청입니다.")
                .addHeader("Content-Type", "application/json")
                .setBodyDelay(0, TimeUnit.MILLISECONDS));

        // when & then
        ExternalClientException exception = assertThrows(ExternalClientException.class, () ->
                service.getSync("/test", String.class, null)
        );
    }

    @Test
    void getSync_shouldThrowServerException() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Error"));

        // when + then
        ExternalServerException e = assertThrows(ExternalServerException.class, () ->
                service.getSync("/error", String.class, null));
        assertTrue(e.getMessage().contains("500"));
    }

    @Test
    void getSync_shouldTimeout() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("Delayed").setBodyDelay(10, TimeUnit.SECONDS));

        // when + then
        assertThrows(ExternalTimeoutException.class, () ->
                service.getSync("/timeout", String.class, null));
    }

}
