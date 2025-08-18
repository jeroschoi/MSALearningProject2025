package com.event.msalearningproject.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class MultiMessageResponse {
    private Long totalCount;
    private Long sentCount;
    private Long failedCount;
    private List<MessageRequestDto> failedMessages;
}
