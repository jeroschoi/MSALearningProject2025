package com.event.msalearningproject.webclient;

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
    void postSync_shouldReturnExpectedResponse() {
        // given
        String expected = "pong";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.postSync(
                "/test",
                expected,
                String.class,
                null
        );

        // then
        assertEquals(expected, actual);
    }

    @Test
    void postAsync_shouldReturnExpectedResponse() {
        // given
        String expected = "pong";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.postAsync(
                "/test",
                expected,
                String.class,
                null
        ).block();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void putSync_shouldReturnExpectedResponse() {
        // given
        String expected = "new data";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.putSync(
                "/test",
                "new data",   // requestBody (업데이트할 내용)
                String.class,
                null
        );

        // then
        assertEquals(expected, actual);
    }

    @Test
    void putAsync_shouldReturnExpectedResponse() {
        // given
        String expected = "updated";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.putAsync(
                "/test",
                "updated",
                String.class,
                null
        ).block();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void patchSync_shouldReturnExpectedResponse() {
        // given
        String expected = "patched";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.patchSync(
                "/test",
                "partial update",  // requestBody
                String.class,
                null
        );

        // then
        assertEquals(expected, actual);
    }

    @Test
    void patchAsync_shouldReturnExpectedResponse() {
        // given
        String expected = "patched";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expected)
                .addHeader("Content-Type", "application/json"));

        // when
        String actual = service.patchAsync(
                "/test",
                "partial update",
                String.class,
                null
        ).block();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void deleteSync_shouldReturnExpectedResponse() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));
        // when
        String actual = service.deleteSync("/test", String.class, null);

        // then
        assertNull(actual);
    }

    @Test
    void deleteAsync_shouldReturnExpectedResponse() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));
        // when
        String actual = service.deleteAsync(
                "/test",
                String.class,
                null).block();

        // then
        assertNull(actual);
    }
}
