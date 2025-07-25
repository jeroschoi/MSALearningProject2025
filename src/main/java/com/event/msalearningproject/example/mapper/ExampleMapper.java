package com.event.msalearningproject.example.mapper;


import com.event.msalearningproject.example.dto.SampleDto;
import com.event.msalearningproject.example.entity.SampleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExampleMapper {

    ExampleMapper INSTANCE = Mappers.getMapper(ExampleMapper.class);

     SampleDto sampleEntityToDto(SampleEntity entity);
     SampleEntity sampleDtoToEntity(SampleDto dto);
}
