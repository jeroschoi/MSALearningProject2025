package com.event.msalearningproject.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@ToString
@Getter
@Table(name = "member")
@Entity
@Builder
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column
    private MessageType messageType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @Column
    private LocalDateTime exitDate;

    @Column(nullable = false)
    private Boolean active = true;

}
