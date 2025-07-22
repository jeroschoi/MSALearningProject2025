package com.event.msalearningproject.message.service;

import com.event.msalearningproject.message.entity.MessageHistory;
import com.event.msalearningproject.message.repository.MessageRespository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRespository repository;


    /**
     * 휴대폰 번호 로 메시지 이력 주회
     * @param phoneNumber 휴대폰 번호
     * @return 메시지 이력 리스트
     */
    public List<MessageHistory> getMessagePhoneNumber(String phoneNumber) {
        List<MessageHistory> result = repository.findByPhoneNumberOrderBySentAtDesc(phoneNumber);
        log.info("메시지 이력 조회 - 휴대폰 번호: {}, 결과: {}", phoneNumber, result);
        if (result.isEmpty()) {
            log.error("메시지 이력 조회 실패 -휴대폰 번호: {}", phoneNumber);
            throw new EntityNotFoundException("No message history found for phone number: " + phoneNumber);
        }
        return result;
    }

    /**
     * 회원 ID 로 메시지 이력 조회
     * @param memberId 회원 ID
     * @return 메시지 이력 리스트
     */
    public List<MessageHistory> getMessageMemberId(String memberId) {
        List<MessageHistory> result = repository.findByMemberIdOrderBySentAtDesc(memberId);
        log.info("메시지 이력 조회 - 회원 ID: {}, 결과: {}", memberId, result);
        if (result.isEmpty()) {
            log.error("메시지 이력 조회 실패 - 회원 ID: {}", memberId);
            throw new EntityNotFoundException("No message history found for member id: " + memberId);
        }
        return result;
    }


}
