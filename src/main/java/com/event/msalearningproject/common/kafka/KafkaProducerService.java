package com.event.msalearningproject.common.kafka;

import com.event.msalearningproject.common.kafka.dto.KafkaMessage;
import com.event.msalearningproject.common.logging.LoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 메시지 생산 서비스
 * 모든 도메인에서 공통으로 사용할 수 있는 Kafka 메시지 전송 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final LoggingService loggingService;

    /**
     * 메시지 전송 (비동기)
     * 가장 빠름, 결과를 나중에 확인
     */
    public <T> CompletableFuture<SendResult<String, String>> sendMessageAsync(String topic, String key, T payload, String traceId) {
        try {
            KafkaMessage<T> kafkaMessage = KafkaMessage.of(topic, key, payload, traceId);
            String messageJson = objectMapper.writeValueAsString(kafkaMessage);
            
            loggingService.logBusinessLogic(traceId, "KafkaProducerService", "sendMessageAsync", 
                    "Sending message to topic: " + topic + ", key: " + key);
            
            return kafkaTemplate.send(topic, key, messageJson)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            // 메시지 전송 실패 시 로깅
                            loggingService.logError(traceId, "KafkaProducerService", "sendMessageAsync", 
                                    new RuntimeException("Failed to send Kafka message", throwable));
                        } else {
                            // 메시지 전송 성공 시 로깅
                            loggingService.logBusinessLogic(traceId, "KafkaProducerService", "sendMessageAsync", 
                                    "Message sent successfully to partition: " + result.getRecordMetadata().partition() + 
                                    ", offset: " + result.getRecordMetadata().offset());
                        }
                    });
        } catch (JsonProcessingException e) {
            loggingService.logError(traceId, "KafkaProducerService", "sendMessageAsync", e);
            CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    /**
     * 메시지 전송 (동기)
     * 확실하지만 느림, 즉시 결과 확인
     */
    public <T> SendResult<String, String> sendMessageSync(String topic, String key, T payload, String traceId) {
        try {
            KafkaMessage<T> kafkaMessage = KafkaMessage.of(topic, key, payload, traceId);
            String messageJson = objectMapper.writeValueAsString(kafkaMessage);
            
            loggingService.logBusinessLogic(traceId, "KafkaProducerService", "sendMessageSync", 
                    "Sending message to topic: " + topic + ", key: " + key);
            
            SendResult<String, String> result = kafkaTemplate.send(topic, key, messageJson).get();
            
            loggingService.logBusinessLogic(traceId, "KafkaProducerService", "sendMessageSync", 
                    "Message sent successfully to partition: " + result.getRecordMetadata().partition() + 
                    ", offset: " + result.getRecordMetadata().offset());
            
            return result;
        } catch (Exception e) {
            loggingService.logError(traceId, "KafkaProducerService", "sendMessageSync", e);
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    /**
     * 메시지 전송 (Fire and Forget)
     * 가장 빠름, 결과 상관없음
     */
    public <T> void sendMessage(String topic, String key, T payload, String traceId) {
        try {
            KafkaMessage<T> kafkaMessage = KafkaMessage.of(topic, key, payload, traceId);
            String messageJson = objectMapper.writeValueAsString(kafkaMessage);
            
            loggingService.logBusinessLogic(traceId, "KafkaProducerService", "sendMessage", 
                    "Sending message to topic: " + topic + ", key: " + key);
            
            // 보내고 끝
            kafkaTemplate.send(topic, key, messageJson);
        } catch (JsonProcessingException e) {
            loggingService.logError(traceId, "KafkaProducerService", "sendMessage", e);
            throw new RuntimeException("Failed to serialize Kafka message", e);
        }
    }
} 