package com.event.msalearningproject.message.service;

import com.event.msalearningproject.example.dto.SampleDto;
import com.event.msalearningproject.example.entity.SampleEntity;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
import com.event.msalearningproject.message.entity.MessageType;
import com.event.msalearningproject.message.mapper.MessageMapper;
import com.event.msalearningproject.message.repository.MessageRespository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRespository repository;


    /**
     * 메시지 이력 저장
     * @param dto 메시지 요청 DTO
     * @return 저장된 메시지 이력 엔티티
     */
    @Transactional
    public MessageHistory saveMessageHistory(MessageRequestDto dto) {
        log.info("메시지 이력 저장 - Request: {}", dto);
        MessageHistory entity = MessageMapper.INSTANCE.messageDtoToEntity(dto);
        return repository.save(entity);
    }

    /**
     * 휴대폰 번호 로 메시지 이력 주회
     * @param phoneNumber 휴대폰 번호
     * @return 메시지 이력 리스트
     */
    @Transactional(readOnly = true) // ReadOnly 트랜잭션을 사용하여 성능 최적화
    public List<MessageHistory> getMessagePhoneNumber(String phoneNumber) {
        List<MessageHistory> result = repository.findByPhoneNumberAndVisibleTrueOrderBySentAtDesc(phoneNumber);
        if (result.isEmpty()) {
            log.error("메시지 이력 조회 실패 -휴대폰 번호: {}", phoneNumber);
            throw new EntityNotFoundException("No message history found for phone number: " + phoneNumber);
        }
        log.info("메시지 이력 조회 - 휴대폰 번호: {}, 결과: {}", phoneNumber, result.get(0));
        return result;
    }

    /**
     * 회원 ID 로 메시지 이력 조회
     * @param memberId 회원 ID
     * @return 메시지 이력 리스트
     */
    @Transactional(readOnly = true)
    public List<MessageHistory> getMessageMemberId(String memberId) {
        List<MessageHistory> result = repository.findByMemberIdAndVisibleTrueOrderBySentAtDesc(memberId);
        if (result.isEmpty()) {
            log.error("메시지 이력 조회 실패 - 회원 ID: {}", memberId);
            throw new EntityNotFoundException("No message history found for member id: " + memberId);
        }
        log.info("메시지 이력 조회 - 회원 ID: {}, 결과: {}", memberId, result.get(0));
        return result;
    }

    /**
     * 회원 탈퇴시 메시지 이력 삭제
     * visible false
     * @param memberId 회원 ID
     */
    @Transactional
    public int visableFalseMessageHistory(String memberId) {
        List<MessageHistory> result = repository.findByMemberIdAndVisibleTrueOrderBySentAtDesc(memberId);
        result.forEach(messageHistory -> {
            messageHistory.setVisible(false);
            repository.save(messageHistory);
            log.info("메시지 이력 삭제 - 회원 ID: {}, 메시지 ID: {}", memberId, messageHistory.getId());
        });
        if (result.isEmpty()) {
            log.error("메시지 이력 삭제 실패 - 회원 ID: {}", memberId);
            return 0;
        }
        log.info("메시지 이력 삭제 완료 - 회원 ID: {} 비활성화 개수 : {}", memberId, result.size());
        return result.size();
    }


}
