package com.event.msalearningproject.message.dto;

import com.event.msalearningproject.message.entity.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {

    @Schema(description = "회원아이디",example = "user123")
    @NotBlank(message = "회원 ID는 필수입니다.")
    private String memberId;

    @Schema(description = "회원 연락처", example = "010-1234-5678")
    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^01[0-9]{1}-?[0-9]{3,4}-?[0-9]{4}$", message = "연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phoneNumber;

    @Schema(description = "메시지 내용", example = "안녕하세요, 이벤트에 참여해주셔서 감사합니다!")
    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 300, message = "메시지 내용은 1자 이상 300자 이하로 입력해주세요.")
    private String content;

    @Schema(description = "메시지 타입 (PUSH, KAKAO, SMS, EMAIL)", example = "PUSH")
    @NotNull(message = "메시지 타입은 필수입니다.")
    private MessageType messageType; // 메시지 타입 (PUSH, KAKAO, SMS, EMAIL)
}
