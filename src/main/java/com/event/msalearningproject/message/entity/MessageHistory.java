package com.event.msalearningproject.message.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_MESSAGE_HISTORY")
public class MessageHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("회원아이디")
    private String memberId;

    @Comment("회원 연락처")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Comment("메시지 타입")
    private MessageType messageType;

    @Comment("메시지 내용")
    private String content;

    @Comment("메시지 전송 시간")
    private LocalDateTime sentAt;

    @Comment("메시지 노출 여부")
    private boolean visible = true;

    @Override
    public String toString() {
        return "MessageHistory{" +
                "id=" + id +
                ", memberId='" + memberId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", sentAt=" + sentAt +
                ", visible=" + visible +
                '}';
    }
}
