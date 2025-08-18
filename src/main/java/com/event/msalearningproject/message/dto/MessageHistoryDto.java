package com.event.msalearningproject.message.dto;


import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistoryDto {
    private String memberId;
    private String phoneNumber;
    private MessageType messageType;
    private String content;
    private String sentAt;
}
