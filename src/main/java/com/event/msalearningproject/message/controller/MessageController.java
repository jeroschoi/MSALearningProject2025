package com.event.msalearningproject.message.controller;

import com.event.msalearningproject.common.dto.CommonResponse;
import com.event.msalearningproject.message.dto.MessageHistoryDto;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.dto.MessageResponseDto;
import com.event.msalearningproject.message.dto.MultiMessageResponse;
import com.event.msalearningproject.message.service.MessageSendService;
import com.event.msalearningproject.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/msa/v1/message")
public class MessageController {

    private final MessageService service;
    private final MessageSendService messageSendService;


    @PostMapping("/send/single")
    @Operation(summary = "Send Single Message", description = "단건 메시지 전송")
    public ResponseEntity<CommonResponse<Boolean>> sendSingleMessage(@RequestBody @Valid MessageRequestDto requestDto ) {
        log.info("Sending single message: {}", requestDto);
        return ResponseEntity.ok(
                CommonResponse.<Boolean>builder()
                        .data(messageSendService.sendMessage(requestDto))
                        //TODO Message Data enum 처리
                        .message("message send success!")
                        .build()
        );
    }

    @PostMapping("/send/multi")
    @Operation(summary = "Send Multiple Messages", description = "다건 메시지 전송")
    public ResponseEntity<CommonResponse<MultiMessageResponse>> sendMultipleMessages(@RequestBody @Valid List<MessageRequestDto> requestDtoList) {
        return ResponseEntity.ok(
                CommonResponse.<MultiMessageResponse>builder()
                        .data(messageSendService.sendMultiMessage(requestDtoList))
                        .build()
        );
    }


    @PostMapping
    @Operation(summary = "Create Message History", description = "메시지 이력 생성")
    public ResponseEntity<CommonResponse<MessageResponseDto>> createMessageHistory(@RequestBody @Valid MessageRequestDto dto) {
        service.saveMessageHistory(dto);
        return ResponseEntity.ok().body(CommonResponse.<MessageResponseDto>builder()
                .data(MessageResponseDto.builder().build())
                .message("이력생성에 성공하였습니다.")
                .build()
        );
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "Get Message History", description = "회원 ID 로 메시지 이력 조회")
    public ResponseEntity<CommonResponse<List<MessageHistoryDto>>> getMessageHistoryMemberId(@PathVariable String memberId) {
        return ResponseEntity.ok().body(
                CommonResponse.<List<MessageHistoryDto>>builder()
                        .data(service.getMessageMemberId(memberId))
                        .build()
        );
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get Message History by Phone Number", description = "휴대폰 번호로 메시지 이력 조회")
    public ResponseEntity<CommonResponse<List<MessageHistoryDto>>> getMessageHistoryPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok().body(
                CommonResponse.<List<MessageHistoryDto>>builder()
                        .data(service.getMessagePhoneNumber(phoneNumber))
                        .build()
        );
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "Update Message History", description = "회원 ID 로 메시지 이력 비활성화")
    public ResponseEntity<CommonResponse<Boolean>> updateMessageHistory(@PathVariable String memberId) {
        return ResponseEntity.ok().body(CommonResponse.<Boolean>builder()
                .data(service.visibleFalseMessageHistory(memberId))
                .build());
    }
}
