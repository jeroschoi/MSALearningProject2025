//package com.event.msalearningproject.example.service;
//
//import com.event.msalearningproject.example.dto.SampleDto;
//import com.event.msalearningproject.example.entity.SampleEntity;
//import com.event.msalearningproject.example.repository.SampleRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//class ExampleServiceTest {
//
//    @Mock
//    SampleRepository sampleRepository;
//
//    @InjectMocks
//    ExampleService exampleService;
//
//    @Test
//    void sampleSelectSuccessTest() {
//        SampleEntity entity = SampleEntity.builder().id(1L).content("test").build();
//        Mockito.when(sampleRepository.findById(1L)).thenReturn(Optional.of(entity));
//        SampleDto result = exampleService.sampleSelect(1L);
//        assertThat(result.getContent()).isEqualTo("test");
//    }
//
//    @Test
//    void sampleUpdateSuccessTest() {
//        SampleDto dto = SampleDto.builder().id(1L).content("update").build();
//        Mockito.when(sampleRepository.save(any())).thenReturn(
//                SampleEntity.builder().id(1L).content("update").build()
//        );
//        String result = exampleService.sampleUpdate(dto);
//        assertThat(result).isEqualTo("update success");
//    }
//
//    @Test
//    void sampleInsertSuccessTest() {
//        SampleDto dto = SampleDto.builder().content("insert").build();
//        Mockito.when(sampleRepository.save(any())).thenReturn(
//                SampleEntity.builder().id(2L).content("insert").build()
//        );
//        String result = exampleService.sampleInsert(dto);
//        assertThat(result).isEqualTo("insert success");
//    }
//
//    @Test
//    void sampleDeleteSuccessTest() {
//        exampleService.sampleDelete(1L);
//        Mockito.verify(sampleRepository).deleteById(1L);
//    }
//}
