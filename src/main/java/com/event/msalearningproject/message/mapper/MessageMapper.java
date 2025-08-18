package com.event.msalearningproject.message.mapper;


import com.event.msalearningproject.message.dto.MessageHistoryDto;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.repository.entity.MessageHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageHistory messageDtoToEntity(MessageRequestDto dto);
    MessageHistoryDto entityToMessageHistoryDto(MessageHistory entity);
    List<MessageHistoryDto> listEntityToListMessageHistoryDto(List<MessageHistory> listEntity);
}
