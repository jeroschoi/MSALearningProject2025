package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.entity.MemberEntity;
import com.event.msalearningproject.member.entity.MessageHistoryEntity;
import com.event.msalearningproject.member.repository.MessageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageHistoryRepository messageHistoryRepository;

    @Transactional
    public void sendJoinMessage(MemberEntity memberEntity) {
        String content = String.format(
                "%s님 회원가입 완료! 가입일시 : %s",
                memberEntity.getName(),
                memberEntity.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        MessageHistoryEntity messageHistoryEntity = MessageHistoryEntity.builder()
                .member(memberEntity)
                .messageType(memberEntity.getMessageType())
                .content(content)
                .to(memberEntity.getContact())
                .sentAt(LocalDateTime.now())
                .build();

        try {
            if (!sendMessage(messageHistoryEntity)) {
                throw new RuntimeException("메시지 전송 실패");
            }
            messageHistoryEntity.setSent(true);

        } catch (Exception e) {
            messageHistoryEntity.setSent(false);
            messageHistoryEntity.setErrorMessage(e.getMessage());

        } finally {
            messageHistoryRepository.save(messageHistoryEntity);

        }
    }

    public void sendExitMessage(MemberEntity memberEntity) {
        String content = String.format(
                "%s님 회원탈퇴 완료! 탈퇴일시 : %s",
                memberEntity.getName(),
                memberEntity.getExitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        try {
            sendMessage(MessageHistoryEntity.builder()
                    .member(memberEntity)
                    .messageType(memberEntity.getMessageType())
                    .content(content)
                    .to(memberEntity.getContact())
                    .build());
        } catch (Exception e) {
            // TODO log
        }
    }

    // TODO 메세지 전송
    private boolean sendMessage(MessageHistoryEntity messageHistoryEntity) {

        return switch (messageHistoryEntity.getMessageType()) {
            case KAKAO, PUSH, EMAIL, SMS -> true;
            default -> false;
        };
    }
}
