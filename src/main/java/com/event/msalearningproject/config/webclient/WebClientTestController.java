package com.event.msalearningproject.config.webclient;

import com.event.msalearningproject.config.webclient.service.CommonWebClientService;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class WebClientTestController {

    private final CommonWebClientService commonWebClientService;

    private final WebClientTestMessageService webClientTestMessageService;

    @GetMapping("/sync")
    public ResponseEntity<String> testGetSync(@RequestParam String url) {
        String result = commonWebClientService.getSync(url, String.class, null);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/async")
    public Mono<String> testGetAsync(@RequestParam String url) {
        return commonWebClientService.getAsync(url, String.class, null);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendTestMessage(@RequestBody MessageRequestDto dto) {
        try {
            webClientTestMessageService.sendMessage(dto);
            return ResponseEntity.ok("메시지 전송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("메시지 전송 실패: " + e.getMessage());
        }
    }
}
