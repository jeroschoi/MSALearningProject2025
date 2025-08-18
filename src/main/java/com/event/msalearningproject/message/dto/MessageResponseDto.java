package com.event.msalearningproject.message.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private String messageId;
    private String status;
    private String errorMessage;
}
