package com.event.msalearningproject.member.dto;

import com.event.msalearningproject.member.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinRequest {

    @NotBlank(message = "사용자 ID는 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "사용자 ID는 4-20자의 영문자와 숫자만 가능합니다")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 8-20자이며, 영문자와 숫자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String contact;

    @NotNull(message = "메시지 타입은 필수입니다")
    private MessageType messageType;
}