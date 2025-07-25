package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.repository.entity.MessageHistoryEntity;
import com.event.msalearningproject.member.repository.entity.MessageType;
import com.event.msalearningproject.member.repository.MessageHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageHistoryRepository messageHistoryRepository;

    public void sendJoinMessage(MemberEntity memberEntity) {
        sendMessage(memberEntity, "회원가입을 축하합니다!");
    }

    public void sendExitMessage(MemberEntity memberEntity) {
        sendMessage(memberEntity, "회원탈퇴가 완료되었습니다.");
    }

    private void sendMessage(MemberEntity memberEntity, String content) {
        
        String recipient = getRecipientByMessageType(memberEntity);
        
        MessageHistoryEntity.MessageHistoryEntityBuilder builder = MessageHistoryEntity.builder()
                .member(memberEntity)
                .messageType(memberEntity.getMessageType())
                .content(content)
                .to(recipient);

        try {
            
            log.info("메시지 전송: userId={}, messageType={}, recipient={}, content={}", 
                    memberEntity.getUserId(), memberEntity.getMessageType(), recipient, content);
            
            // TODO: 실제 메시지 전송 로직 구현
            
            // 성공
            builder.sent(true);
            
        } catch (Exception e) {
            log.error("메시지 전송 실패: userId={}, error={}", 
                    memberEntity.getUserId(), e.getMessage());
            
            // 실패
            builder.sent(false);
            builder.errorMessage(e.getMessage());
            
            throw new RuntimeException("메시지 전송 실패", e);
        } finally {
            messageHistoryRepository.save(builder.build());
        }
    }

    // 메시지 타입에 따른 수신 설정
    private String getRecipientByMessageType(MemberEntity memberEntity) {
        MessageType messageType = memberEntity.getMessageType();
        
        return switch (messageType) {
            case SMS, KAKAO, PUSH -> memberEntity.getContact();  // 연락처 사용
            case EMAIL -> memberEntity.getEmail();               // 이메일 사용
        };
    }
}
