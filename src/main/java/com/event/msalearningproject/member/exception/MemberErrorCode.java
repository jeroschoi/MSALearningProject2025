package com.event.msalearningproject.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {
    
    // 회원가입 관련 에러
    DUPLICATE_USER_ID("M001", "이미 사용 중인 사용자 ID입니다."),
    DUPLICATE_EMAIL("M002", "이미 사용 중인 이메일입니다."),
    DUPLICATE_CONTACT("M003", "이미 사용 중인 연락처입니다."),
    INVALID_PASSWORD("M004", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_EMAIL("M005", "이메일 형식이 올바르지 않습니다."),
    INVALID_CONTACT("M006", "연락처 형식이 올바르지 않습니다."),
    
    // 회원 조회 관련 에러
    MEMBER_NOT_FOUND("M007", "존재하지 않는 사용자입니다."),
    INACTIVE_MEMBER("M008", "비활성화된 사용자입니다."),
    
    // 회원 탈퇴 관련 에러
    ALREADY_EXITED("M009", "이미 탈퇴한 사용자입니다."),
    
    // 시스템 에러
    DATABASE_ERROR("M010", "데이터베이스 오류가 발생했습니다."),
    MESSAGE_SEND_ERROR("M011", "메시지 전송 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR("M012", "내부 서버 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
} 