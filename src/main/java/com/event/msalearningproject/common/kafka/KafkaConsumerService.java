package com.event.msalearningproject.common.kafka;

import com.event.msalearningproject.common.kafka.dto.KafkaMessage;
import com.event.msalearningproject.common.logging.LoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka 메시지 소비 서비스
 * 모든 도메인에서 공통으로 사용할 수 있는 Kafka 메시지 수신 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final LoggingService loggingService;

    /**
     * 메시지 수신 및 처리
     * 기본 토픽 수신
     */
    @KafkaListener(topics = "${kafka.topic.default}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            // 메시지 역직렬화
            KafkaMessage<?> kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
            String traceId = kafkaMessage.getTraceId();
            
            // 메시지 수신 로깅
            loggingService.logBusinessLogic(traceId, "KafkaConsumerService", "consumeMessage", 
                    "Received message from topic: " + topic + ", messageId: " + kafkaMessage.getMessageId());
            
            // 메세지 처리
            processMessage(kafkaMessage, traceId);
            
        } catch (JsonProcessingException e) {
            loggingService.logError("unknown", "KafkaConsumerService", "consumeMessage", e);
            log.error("Failed to deserialize Kafka message: {}", message, e);
        } catch (Exception e) {
            loggingService.logError("unknown", "KafkaConsumerService", "consumeMessage", e);
            log.error("Error processing Kafka message: {}", message, e);
        }
    }

    /**
     * 메시지 처리 로직
     */
    private void processMessage(KafkaMessage<?> kafkaMessage, String traceId) {
        try {
            // 메시지 타입에 따른 처리 로직
            String messageType = kafkaMessage.getPayload().getClass().getSimpleName();
            
            loggingService.logBusinessLogic(traceId, "KafkaConsumerService", "processMessage", 
                    "Processing message type: " + messageType);
            
            // 향후 메시지 타입별 핸들러 호출
            // 예: memberEventHandler.handle(kafkaMessage), 
            //      messageEventHandler.handle(kafkaMessage)
            
        } catch (Exception e) {
            loggingService.logError(traceId, "KafkaConsumerService", "processMessage", e);
            throw new RuntimeException("Failed to process Kafka message", e);
        }
    }

    /**
     * 회원 관련 토픽 전용 리스너
     */
    @KafkaListener(topics = "${kafka.topic.member}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMemberMessage(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        consumeMessage(message, topic);
    }

    /**
     * 메시지 토픽 전용 리스너
     */
    @KafkaListener(topics = "${kafka.topic.message}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessageTopic(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        consumeMessage(message, topic);
    }
} 