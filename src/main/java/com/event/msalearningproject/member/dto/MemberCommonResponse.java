package com.event.msalearningproject.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberCommonResponse<T> {
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Builder.Default
    private boolean success = true;
    
    private String message;
    
    private T data;
    
    private String errorCode;
    
    // 성공 응답 생성
    public static <T> MemberCommonResponse<T> success(T data) {
        return MemberCommonResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    // 실패 응답 생성
    public static <T> MemberCommonResponse<T> error(String message) {
        return MemberCommonResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static <T> MemberCommonResponse<T> error(String message, String errorCode) {
        return MemberCommonResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
} 