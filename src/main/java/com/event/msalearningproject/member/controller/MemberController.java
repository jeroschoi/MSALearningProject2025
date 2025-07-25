package com.event.msalearningproject.member.controller;

import com.event.msalearningproject.member.dto.MemberCommonResponse;
import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.exception.MemberException;
import com.event.msalearningproject.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.event.msalearningproject.member.exception.MemberErrorCode;

@Slf4j
@RestController
@RequestMapping("/msa/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member Management", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    public ResponseEntity<MemberCommonResponse<MemberResponse>> join(@Valid @RequestBody MemberJoinRequest request) {
        try {
            log.info("회원가입 요청: userId={}, name={}, email={}", 
                    request.getUserId(), request.getName(), request.getEmail());
            
            MemberResponse response = memberService.join(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(MemberCommonResponse.success(response));
            
        } catch (MemberException e) {
            return handleBusinessException(e, request.getUserId(), "회원가입");
        } catch (Exception e) {
            return handleSystemException(e, request.getUserId(), "회원가입");
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 조회 (ID)", description = "사용자 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberCommonResponse<MemberResponse>> findByUserId(
            @Parameter(description = "사용자 ID", example = "testuser")
            @PathVariable String userId) {
        
        log.info("회원 조회 요청 (ID): userId={}", userId);
        
        try {
            MemberResponse member = memberService.findByUserId(userId);
            return ResponseEntity.ok(MemberCommonResponse.success(member));
        } catch (MemberException e) {
            return handleBusinessException(e, userId, "회원 조회");
        } catch (Exception e) {
            return handleSystemException(e, userId, "회원 조회");
        }
    }

    @GetMapping("/contact/{contact}")
    @Operation(summary = "회원 조회 (연락처)", description = "연락처로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberCommonResponse<MemberResponse>> findByContact(
            @Parameter(description = "연락처", example = "010-1234-5678")
            @PathVariable String contact) {
        
        log.info("회원 조회 요청 (연락처): contact={}", contact);
        
        try {
            MemberResponse member = memberService.findByContact(contact);
            return ResponseEntity.ok(MemberCommonResponse.success(member));
        } catch (MemberException e) {
            return handleBusinessException(e, contact, "회원 조회");
        } catch (Exception e) {
            return handleSystemException(e, contact, "회원 조회");
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원탈퇴", description = "회원을 탈퇴 처리합니다.")
    public ResponseEntity<MemberCommonResponse<Void>> exit(
            @Parameter(description = "사용자 ID", example = "testuser")
            @PathVariable String userId) {
        
        try {
            log.info("회원탈퇴 요청: userId={}", userId);
            
            memberService.exit(userId);
            
            return ResponseEntity.ok(MemberCommonResponse.success(null));
            
        } catch (MemberException e) {
            return handleBusinessException(e, userId, "회원탈퇴");
        } catch (Exception e) {
            return handleSystemException(e, userId, "회원탈퇴");
        }
    }

    @GetMapping
    @Operation(summary = "활성 회원 목록 조회", description = "활성 상태인 모든 회원 목록을 조회합니다.")
    public ResponseEntity<MemberCommonResponse<List<MemberResponse>>> getAllActiveMembers() {
        
        try {
            List<MemberResponse> members = memberService.getAllActiveMembers();
            return ResponseEntity.ok(MemberCommonResponse.success(members));
        } catch (Exception e) {
            return handleSystemException(e, "ALL", "활성 회원 목록 조회");
        }
    }

    
    /**
     * 비즈니스 예외 처리 (MemberException)
     */
    private <T> ResponseEntity<MemberCommonResponse<T>> handleBusinessException(MemberException e, String userId, String operation) {
        String errorCode = e.getErrorCode().getCode();
        String errorMessage = e.getMessage();
        
        log.warn("비즈니스 예외 발생: operation={}, userId={}, errorCode={}, message={}", 
                operation, userId, errorCode, errorMessage);
        
        HttpStatus status = determineHttpStatus(e.getErrorCode());
        return ResponseEntity.status(status)
                .body(MemberCommonResponse.error(errorMessage, errorCode));
    }

    /**
     * 시스템 예외 처리 (일반 Exception)
     */
    private <T> ResponseEntity<MemberCommonResponse<T>> handleSystemException(Exception e, String userId, String operation) {
        log.error("시스템 예외 발생: operation={}, userId={}, exception={}", 
                operation, userId, e.getClass().getSimpleName(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MemberCommonResponse.error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    /**
     * 에러 코드에 따른 HTTP Status 결정
     */
    private HttpStatus determineHttpStatus(MemberErrorCode errorCode) {
        return switch (errorCode) {
            case DUPLICATE_USER_ID, DUPLICATE_EMAIL, DUPLICATE_CONTACT -> HttpStatus.CONFLICT; // 409
            case MEMBER_NOT_FOUND -> HttpStatus.NOT_FOUND; // 404
            case ALREADY_EXITED -> HttpStatus.BAD_REQUEST; // 400
            case DATABASE_ERROR, INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR; // 500
            default -> HttpStatus.BAD_REQUEST; // 400
        };
    }

    /**
     * 입력값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MemberCommonResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = extractValidationErrorMessage(ex);
        
        log.warn("입력값 검증 실패: errors={}", ex.getBindingResult().getFieldErrors());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MemberCommonResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    /**
     * Validation 에러 메시지 추출
     */
    private String extractValidationErrorMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("입력값 검증에 실패했습니다.");
    }
}
