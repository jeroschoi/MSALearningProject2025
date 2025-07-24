package com.event.msalearningproject.message.controller;

import com.event.msalearningproject.message.dto.GlobalReponseDto;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageHistory;
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
    public ResponseEntity<GlobalReponseDto> sendSingleMessage(@RequestBody @Valid MessageRequestDto requestDto ) {
        GlobalReponseDto responseDto = new GlobalReponseDto();

        boolean isSent = messageSendService.sendMessage(requestDto);
        if (isSent) {
            log.info("Message sent successfully: {}", requestDto);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            responseDto.setData(result);
            return ResponseEntity.ok(responseDto);
        } else {
            log.warn("Message sending failed: {}", requestDto);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "failed");
            responseDto.setData(result);
            return ResponseEntity.status(500).body(responseDto);
        }
    }

    @PostMapping("/send/multi")
    @Operation(summary = "Send Multiple Messages", description = "다건 메시지 전송")
    public ResponseEntity<GlobalReponseDto> sendMultipleMessages(@RequestBody @Valid List<MessageRequestDto> requestDtoList) {
        GlobalReponseDto responseDto = new GlobalReponseDto();
        int sentCount = 0;
        List<MessageRequestDto> failedList = new ArrayList<>();

        for (MessageRequestDto dto : requestDtoList) {
            log.info("Sending message: {}", dto);
            try {
                boolean isSent = messageSendService.sendMessage(dto);
                if (isSent) {
                    log.info("Message sent successfully: {}", dto);
                    sentCount++;
                } else {
                    log.warn("Message failed to send: {}", dto);
                    failedList.add(dto);
                }
            } catch (Exception e) {
                log.error("Exception while sending message: {}", dto, e);
                failedList.add(dto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", requestDtoList.size());
        result.put("sentCount", sentCount);
        result.put("failedCount", failedList.size());

        responseDto.setData(result);
        return ResponseEntity.ok(responseDto);
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
        int updateSize = service.visableFalseMessageHistory(memberId);
        GlobalReponseDto responseDto = new GlobalReponseDto();
        responseDto.setData(new HashMap<String, Integer>() {{;
            put("updateSize", updateSize);
        }});
        return ResponseEntity.ok().body(responseDto);
    }
}
