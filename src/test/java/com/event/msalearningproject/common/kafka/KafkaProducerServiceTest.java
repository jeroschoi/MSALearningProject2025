package com.event.msalearningproject.common.kafka;

import com.event.msalearningproject.common.logging.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private String topic;
    private String key;
    private Object payload;
    private String traceId;
    private String messageJson;

    @BeforeEach
    void setUp() {
        topic = "test-topic";
        key = "test-key";
        payload = "test-payload";
        traceId = "test-trace-id";
        messageJson = "{\"messageId\":\"test\",\"payload\":\"test-payload\"}";
    }

    @Test
    @DisplayName("비동기 메시지 전송 성공 테스트")
    void testSendMessageAsync_Success() throws Exception {
        // given
        SendResult<String, String> sendResult = createMockSendResult();
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        
        when(objectMapper.writeValueAsString(any())).thenReturn(messageJson);
        when(kafkaTemplate.send(eq(topic), eq(key), eq(messageJson))).thenReturn(future);

        // when
        CompletableFuture<SendResult<String, String>> result = kafkaProducerService.sendMessageAsync(topic, key, payload, traceId);

        // then
        assert result.get() == sendResult;
        verify(loggingService).logBusinessLogic(eq(traceId), eq("KafkaProducerService"), eq("sendMessageAsync"), anyString());
    }

    @Test
    @DisplayName("동기 메시지 전송 성공 테스트")
    void testSendMessageSync_Success() throws Exception {
        // given
        SendResult<String, String> sendResult = createMockSendResult();
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);
        
        when(objectMapper.writeValueAsString(any())).thenReturn(messageJson);
        when(kafkaTemplate.send(eq(topic), eq(key), eq(messageJson))).thenReturn(future);

        // when
        SendResult<String, String> result = kafkaProducerService.sendMessageSync(topic, key, payload, traceId);

        // then
        assert result == sendResult;
        verify(loggingService).logBusinessLogic(eq(traceId), eq("KafkaProducerService"), eq("sendMessageSync"), anyString());
    }

    @Test
    @DisplayName("Fire and Forget 메시지 전송 테스트")
    void testSendMessage() throws Exception {
        // given
        when(objectMapper.writeValueAsString(any())).thenReturn(messageJson);

        // when
        kafkaProducerService.sendMessage(topic, key, payload, traceId);

        // then
        verify(kafkaTemplate).send(eq(topic), eq(key), eq(messageJson));
        verify(loggingService).logBusinessLogic(eq(traceId), eq("KafkaProducerService"), eq("sendMessage"), anyString());
    }

    @Test
    @DisplayName("JSON 직렬화 실패 테스트")
    void testSendMessage_JsonProcessingException() throws Exception {
        // given
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("JSON processing error"));

        // when & then
        try {
            kafkaProducerService.sendMessage(topic, key, payload, traceId);
            assert false; // 예외가 발생해야 함
        } catch (RuntimeException e) {
            assert e.getMessage().contains("Failed to serialize Kafka message");
            verify(loggingService).logError(eq(traceId), eq("KafkaProducerService"), eq("sendMessage"), any(RuntimeException.class));
        }
    }

    private SendResult<String, String> createMockSendResult() {
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 0, 0, 0L, 0L, 0, 0);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, messageJson);
        return new SendResult<>(producerRecord, recordMetadata);
    }
} 