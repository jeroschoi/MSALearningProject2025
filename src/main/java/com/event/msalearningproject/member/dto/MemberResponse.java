package com.event.msalearningproject.member.dto;

import com.event.msalearningproject.member.entity.MemberEntity;
import com.event.msalearningproject.member.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private Long id;
    private String userId;
    private String contact;
    private MessageType messageType;
    private String name;
    private String email;
    private String address;
    private LocalDateTime joinDate;
    private LocalDateTime withdrawDate;
    private boolean active;
    private LocalDateTime updatedAt;

    public static MemberResponse from(MemberEntity member) {
        return MemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .name(member.getName())
                .contact(member.getContact())
                .messageType(member.getMessageType())
                .joinDate(member.getJoinDate())
                .build();
    }
}