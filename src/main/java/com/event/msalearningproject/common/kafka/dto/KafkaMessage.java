package com.event.msalearningproject.common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kafka 메시지 공통 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage<T> {
    
    private String messageId; // 메시지 고유 ID
    private String topic; // 토픽 이름
    private String key; // 메시지 키
    private T payload; // 메시지 내용
    private LocalDateTime timestamp;
    private String traceId;
    
    public static <T> KafkaMessage<T> of(String topic, String key, T payload, String traceId) {
        return KafkaMessage.<T>builder()
                .messageId(java.util.UUID.randomUUID().toString()) // 메시지 고유 ID 생성
                .topic(topic)
                .key(key)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }
} 