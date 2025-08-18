package com.event.msalearningproject.member.controller;

import com.event.msalearningproject.common.dto.CommonResponse;
import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/msa/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member Management", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    public ResponseEntity<CommonResponse<MemberResponse>> join(@Valid @RequestBody MemberJoinRequest request) {
        log.info("회원가입 요청: userId={}, name={}, email={}",
                request.getUserId(), request.getName(), request.getEmail());
        return ResponseEntity.ok(
                CommonResponse.<MemberResponse>builder()
                        .data(memberService.join(request))
                        .build()
        );
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 조회 (ID)", description = "사용자 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<CommonResponse<MemberResponse>> findByUserId(
            @Parameter(description = "사용자 ID", example = "testUser")
            @PathVariable String userId) {
        log.info("회원 조회 요청 (ID): userId={}", userId);
        return ResponseEntity.ok(
                CommonResponse.<MemberResponse>builder()
                .data(memberService.findByUserId(userId))
                .build()
        );
    }

    @GetMapping("/contact/{contact}")
    @Operation(summary = "회원 조회 (연락처)", description = "연락처로 회원 정보를 조회합니다.")
    public ResponseEntity<CommonResponse<MemberResponse>> findByContact(
            @Parameter(description = "연락처", example = "010-1234-5678")
            @PathVariable String contact) {
            log.info("회원 조회 요청 (연락처): contact={}", contact);
            return ResponseEntity.ok(
                    CommonResponse.<MemberResponse>builder()
                    .data(memberService.findByContact(contact))
                    .build()
            );
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원탈퇴", description = "회원을 탈퇴 처리합니다.")
    public ResponseEntity<CommonResponse<Void>> exit(
            @Parameter(description = "사용자 ID", example = "testUser")
            @PathVariable String userId) {
            log.info("회원탈퇴 요청: userId={}", userId);

            memberService.exit(userId);

            return ResponseEntity.ok()
                    .body(CommonResponse.<Void>builder()
                            .message("회원탈퇴가 완료되었습니다.")
                            .build());
        }

    @GetMapping
    @Operation(summary = "활성 회원 목록 조회", description = "활성 상태인 모든 회원 목록을 조회합니다.")
    public ResponseEntity<CommonResponse<List<MemberResponse>>> getAllActiveMembers() {
            return ResponseEntity.ok(
                    CommonResponse.<List<MemberResponse>>builder()
                            .data(memberService.getAllActiveMembers())
                            .build());
    }
}

