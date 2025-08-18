package com.event.msalearningproject.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private T data;
    private String errorCode;
}