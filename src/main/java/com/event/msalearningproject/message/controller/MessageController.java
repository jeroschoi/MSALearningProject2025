package com.event.msalearningproject.message.controller;

import com.event.msalearningproject.common.dto.CommonResponse;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.dto.MultiMessageResponse;
import com.event.msalearningproject.message.repository.entity.MessageHistory;
import com.event.msalearningproject.message.service.MessageSendService;
import com.event.msalearningproject.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<GlobalReponseDto> createMessageHistory(@RequestBody @Valid MessageRequestDto dto) {
        MessageHistory messageHistory = service.saveMessageHistory(dto);
        GlobalReponseDto responseDto = new GlobalReponseDto();
        responseDto.setData(messageHistory);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "Get Message History", description = "회원 ID 로 메시지 이력 조회")
    public ResponseEntity<GlobalReponseDto> getMessagetHitsroyMemberId(@PathVariable String memberId) {
        List<MessageHistory> messageHistoryList = service.getMessageMemberId(memberId);
        GlobalReponseDto responseDto = new GlobalReponseDto();
        responseDto.setData(messageHistoryList);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get Message History by Phone Number", description = "휴대폰 번호로 메시지 이력 조회")
    public ResponseEntity<GlobalReponseDto> getMessageHistoryPhoneNumber(@PathVariable String phoneNumber) {
        List<MessageHistory> messageHistoryList = service.getMessagePhoneNumber(phoneNumber);
        GlobalReponseDto responseDto = new GlobalReponseDto();
        responseDto.setData(messageHistoryList);
        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "Update Message History", description = "회원 ID 로 메시지 이력 비활성화")
    public ResponseEntity<GlobalReponseDto> updateMessageHistory(@PathVariable String memberId) {
        int updateSize = service.visibleFalseMessageHistory(memberId);
        GlobalReponseDto responseDto = new GlobalReponseDto();
        responseDto.setData(new HashMap<String, Integer>() {{
            put("updateSize", updateSize);
        }});
        return ResponseEntity.ok().body(responseDto);
    }
}
