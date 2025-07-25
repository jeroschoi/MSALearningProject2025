package com.event.msalearningproject.example.service;

import com.event.msalearningproject.example.dto.SampleDto;
import com.event.msalearningproject.example.entity.SampleEntity;
import com.event.msalearningproject.example.mapper.ExampleMapper;
import com.event.msalearningproject.example.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExampleService {

    final private SampleRepository sampleRepository;

    public SampleDto sampleSelect(Long id) {
        return ExampleMapper.INSTANCE.sampleEntityToDto(sampleRepository.findById(id).orElse(null));
    }

    @Transactional
    public String sampleUpdate(SampleDto sampleDto) {
        log.info("sampleUpdate data : {}", sampleDto.toString());
        sampleRepository.save(ExampleMapper.INSTANCE.sampleDtoToEntity(sampleDto));
        return "update success";
    }

    @Transactional
    public String sampleInsert(SampleDto sampleDto) {
        log.info("sampleInsert data : {}", sampleDto.toString());
        sampleRepository.save(SampleEntity.builder()
                .content(sampleDto.getContent())
                .build());
        return "insert success";
    }

    @Transactional
    public void sampleDelete(Long id) {
        sampleRepository.deleteById(id);
    }
}
