package com.event.msalearningproject.config.webclient.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface CommonWebClientService {

    // GET
    <T> T getSync(String url, Class<T> responseType, Map<String, String> headers);
    <T> Mono<T> getAsync(String url, Class<T> responseType, Map<String, String> headers);

    // POST
    <T, R> R postSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);
    <T, R> Mono<R> postAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);

    // PUT
    <T, R> R putSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);
    <T, R> Mono<R> putAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);

    // PATCH
    <T, R> R patchSync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);
    <T, R> Mono<R> patchAsync(String url, T requestBody, Class<R> responseType, Map<String, String> headers);

    // DELETE
    <T> T deleteSync(String url, Class<T> responseType, Map<String, String> headers);
    <T> Mono<T> deleteAsync(String url, Class<T> responseType, Map<String, String> headers);

}
