package com.event.msalearningproject.config.webclient.service;

import com.event.msalearningproject.config.webclient.exception.ExternalClientException;
import com.event.msalearningproject.config.webclient.exception.ExternalServerException;
import com.event.msalearningproject.config.webclient.exception.ExternalServiceException;
import com.event.msalearningproject.config.webclient.exception.ExternalTimeoutException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonWebClientServiceImpl implements CommonWebClientService {

    private final WebClient webClient;

    private final Retry externalServiceRetry;

    private final CircuitBreaker externalServiceBreaker;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    // 헤더 적용
    private void applyHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
    }

    @Override
    public <T> T getSync(String url, Class<T> responseType, Map<String, String> headers) {
        try {
            return webClient
                    .get()
                    .uri(url)
                    .headers(h -> applyHeaders(h, headers))//헤더 설정
                    .retrieve() // 응답 추출 시작
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("클라이언트 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalClientException(url, response.statusCode().value(), body)
                                    ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("서버 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalServerException(url , response.statusCode().value() ,body)
                                    ))
                    )
                    .bodyToMono(responseType)//응답 타입
                    .transform(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷 브레이커
                    .transform(RetryOperator.of(externalServiceRetry))// retry
                    .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                    .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e))
                    //.timeout(TIMEOUT)
                    .block();
        } catch (Exception e) {
            log.error("GET Sync 호출 실패: {}", url, e);
            throw handleSyncException("GET", url, e);
        }
    }

    @Override
    public <T> Mono<T> getAsync(String url, Class<T> responseType, Map<String, String> headers) {
        return webClient
                .get()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("클라이언트 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalClientException(url, response.statusCode().value(), body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("서버 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalServerException(url , response.statusCode().value() ,body)
                                ))
                )
                .bodyToMono(responseType) // 응답 타입
                .timeout(TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷
                .transformDeferred(RetryOperator.of(externalServiceRetry))           // 커스텀 retry
                .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e));
    }

    @Override
    public <T, R> R postSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        try {
            return webClient
                    .post()
                    .uri(url)
                    .headers(h -> applyHeaders(h, headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("클라이언트 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalClientException(url, response.statusCode().value(), body)
                                    ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("서버 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalServerException(url , response.statusCode().value() ,body)
                                    ))
                    )
                    .bodyToMono(responseType)
                    .timeout(TIMEOUT)
                    .transform(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷 브레이커
                    .transform(RetryOperator.of(externalServiceRetry))// retry
                    .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                    .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e))
                    .block();
        } catch (Exception e) {
            log.error("POST Sync 호출 실패: {}", url, e);
            throw handleSyncException("POST", url, e);
        }
    }

    @Override
    public <T, R> Mono<R> postAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        return webClient
                .post()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("클라이언트 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalClientException(url, response.statusCode().value(), body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("서버 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalServerException(url , response.statusCode().value() ,body)
                                ))
                )
                .bodyToMono(responseType)
                .timeout(TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷
                .transformDeferred(RetryOperator.of(externalServiceRetry))           // 커스텀 retry
                .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e));
    }

    @Override
    public <T, R> R putSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        try {
            return webClient
                    .put()
                    .uri(url)
                    .headers(h -> applyHeaders(h, headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("클라이언트 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalClientException(url, response.statusCode().value(), body)
                                    ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("서버 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalServerException(url , response.statusCode().value() ,body)
                                    ))
                    )
                    .bodyToMono(responseType)
                    .timeout(TIMEOUT)
                    .transform(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷 브레이커
                    .transform(RetryOperator.of(externalServiceRetry))// retry
                    .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                    .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e))
                    .block();
        } catch (Exception e) {
            log.error("PUT Sync 호출 실패: {}", url, e);
            throw handleSyncException("PUT", url, e);
        }
    }

    @Override
    public <T, R> Mono<R> putAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        return webClient
                .put()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("클라이언트 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalClientException(url, response.statusCode().value(), body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("서버 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalServerException(url , response.statusCode().value() ,body)
                                ))
                )
                .bodyToMono(responseType)
                .timeout(TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷
                .transformDeferred(RetryOperator.of(externalServiceRetry))           // 커스텀 retry
                .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e));
    }

    @Override
    public <T, R> R patchSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        try {
            return webClient
                    .patch()
                    .uri(url)
                    .headers(h -> applyHeaders(h, headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("클라이언트 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalClientException(url, response.statusCode().value(), body)
                                    ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("서버 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalServerException(url , response.statusCode().value() ,body)
                                    ))
                    )
                    .bodyToMono(responseType)
                    .timeout(TIMEOUT)
                    .transform(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷 브레이커
                    .transform(RetryOperator.of(externalServiceRetry))// retry
                    .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                    .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e))
                    .block();
        } catch (Exception e) {
            log.error("PATCH Sync 호출 실패: {}", url, e);
            throw handleSyncException("PATCH",url,e);
        }
    }

    @Override
    public <T, R> Mono<R> patchAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers) {
        return webClient
                .patch()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("클라이언트 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalClientException(url, response.statusCode().value(), body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("서버 에러 발생")
                                .flatMap(body -> Mono.error(
                                        new ExternalServerException(url , response.statusCode().value() ,body)
                                ))
                )
                .bodyToMono(responseType)
                .timeout(TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷
                .transformDeferred(RetryOperator.of(externalServiceRetry))           // 커스텀 retry
                .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e));
    }

    @Override
    public <T> T deleteSync(String url, Class<T> responseType, Map<String, String> headers) {
        try {
            return webClient
                    .delete()
                    .uri(url)
                    .headers(h -> applyHeaders(h, headers))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("클라이언트 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalClientException(url, response.statusCode().value(), body)
                                    ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("서버 에러 발생")
                                    .flatMap(body -> Mono.error(
                                            new ExternalServerException(url , response.statusCode().value() ,body)
                                    ))
                    )
                    .bodyToMono(responseType)
                    .timeout(TIMEOUT)
                    .transform(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷 브레이커
                    .transform(RetryOperator.of(externalServiceRetry))// retry
                    .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                    .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e))
                    .block();
        } catch (Exception e) {
            log.error("DELETE Sync 호출 실패: {}", url, e);
            throw handleSyncException("DELETE",url,e);
        }
    }

    @Override
    public <T> Mono<T> deleteAsync(String url, Class<T> responseType, Map<String, String> headers) {
        return webClient
                .delete()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("Client error occurred")
                                .flatMap(body -> Mono.error(
                                        new ExternalClientException(url, response.statusCode().value(), body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("Server error occurred")
                                .flatMap(body -> Mono.error(
                                        new ExternalServerException(url , response.statusCode().value() ,body)
                                ))
                )
                .bodyToMono(responseType)
                .timeout(TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(externalServiceBreaker)) // 서킷
                .transformDeferred(RetryOperator.of(externalServiceRetry))           // 커스텀 retry
                .onErrorMap(TimeoutException.class, e -> new ExternalTimeoutException(url, e))
                .onErrorMap(WebClientRequestException.class, e -> new ExternalServiceException("Connection error to " + url, e));
    }

    // 동기용 예외처리
    private RuntimeException handleSyncException(String method, String url, Exception e) {
        if (e instanceof TimeoutException) {
            return new ExternalTimeoutException(url, e);
        } else if (e instanceof WebClientRequestException) {
            return new ExternalServiceException("[" + method + "] Connection error to " + url, e);
        } else if (e instanceof ExternalServiceException) {
            return (ExternalServiceException) e;
        } else if (e.getCause() instanceof ExternalServiceException) {
            return (ExternalServiceException) e.getCause();
        } else if (e.getCause() instanceof ExternalClientException) {
            return (ExternalClientException) e.getCause();
        } else if (e.getCause() instanceof ExternalServerException) {
            return (ExternalServerException) e.getCause();
        } else if (e.getCause() instanceof ExternalTimeoutException) {
            return (ExternalTimeoutException) e.getCause();
        } else {
            return new ExternalServiceException("[" + method + "] Unknown error at " + url, e);
        }
    }

}
