package com.event.msalearningproject.example.controller;

import com.event.msalearningproject.example.dto.SampleDto;
import com.event.msalearningproject.example.service.ExampleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/msa/v1")
public class ExampleController {

    final private ExampleService exampleService;

    @GetMapping("/sample")
    @Operation(summary = "Example API", description = "Example API  to Select Sample Data")
    public ResponseEntity<SampleDto> sampleSelect(Long id) {
        log.info("sampleSelect API Start");
        return ResponseEntity.ok(exampleService.sampleSelect(id));
    }

    @Operation(summary = "Example API", description = "Example API to update Sample example")
    @PatchMapping("/sample")
    public ResponseEntity<String> sampleUpdate(@RequestParam Long id ,
                                             @RequestParam String content){
        log.info("sampleUpdate API Start");
        return ResponseEntity.ok(
                exampleService.sampleUpdate(
                        SampleDto.builder()
                                 .id(id)
                                 .content(content)
                                 .build()
                ));
    }

    @Operation(summary = "Example API", description = "Example API to Insert Sample example")
    @PostMapping("/sample")
    public ResponseEntity<String> sampleInsert(@RequestParam String content){
        log.info("sampleInsert API Start");
        return ResponseEntity.ok(
                exampleService.sampleInsert(
                        SampleDto.builder()
                                .content(content)
                                .build()
                ));
    }

    @DeleteMapping("/sample")
    @Operation(summary = "Example API", description = "Example API to Delete Sample Data")
    public ResponseEntity<String> sampleDelete(Long id) {
        log.info("sampleDelete API Start");
        exampleService.sampleDelete(id);
        return ResponseEntity.ok("OK");
    }
}
