package com.event.msalearningproject.message.service;


import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
import com.event.msalearningproject.message.mapper.MessageMapper;
import com.event.msalearningproject.message.repository.MessageRespository;
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
        // 010-1234-5678 형식으로 포맷팅
        String formattedNumber = formatPhoneNumber(phoneNumber);
        List<MessageHistory> result = repository.findByPhoneNumberAndVisibleTrueOrderBySentAtDesc(formattedNumber);
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

    /**
     * 휴대폰 번호 포맷팅
     */
    private String formatPhoneNumber(String rawNumber) {
        if (rawNumber == null) return null;

        // 숫자 추출
        String digits = rawNumber.replaceAll("\\D", "");
        log.info("포맷팅 전 휴대폰 번호: {}, 추출된 숫자: {}", rawNumber, digits);

        if (digits.length() == 10) {
            // 010-123-1234
            return digits.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else if (digits.length() == 11) {
            // 010-1234-1234
            return digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else {
            log.error("Invalid phone number format: {}", rawNumber);
            throw new IllegalArgumentException("올바르지 않은 휴대폰 번호입니다: " + rawNumber);
        }
    }

}
