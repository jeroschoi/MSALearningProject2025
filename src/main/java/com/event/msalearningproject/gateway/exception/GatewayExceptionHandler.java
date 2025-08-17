package com.event.msalearningproject.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway에서 발생하는 예외를 처리하는 글로벌 예외 핸들러
 */
@Component
@Order(-1)
@Slf4j
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage = "Internal Server Error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof GatewayException) {
            GatewayException gatewayEx = (GatewayException) ex;
            status = gatewayEx.getStatus();
            errorMessage = gatewayEx.getMessage();
        }

        response.setStatusCode(status);

        String errorResponse = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\"}",
            status.getReasonPhrase(),
            errorMessage,
            status.value(),
            java.time.LocalDateTime.now()
        );

        DataBuffer buffer = response.bufferFactory().wrap(
            errorResponse.getBytes(StandardCharsets.UTF_8)
        );

        log.error("Gateway error: {} - {}", status, errorMessage, ex);
        return response.writeWith(Mono.just(buffer));
    }
}
