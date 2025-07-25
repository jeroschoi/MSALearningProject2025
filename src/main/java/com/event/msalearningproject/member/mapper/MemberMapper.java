package com.event.msalearningproject.member.mapper;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    // Entity → DTO
    @Mapping(target = "address", ignore = true)  // Entity에 없는 필드
    @Mapping(target = "updatedAt", ignore = true)  // Entity에 없는 필드
    MemberResponse toResponse(MemberEntity entity);

    // DTO → Entity (회원가입용)
    @Mapping(target = "id", ignore = true)  // ID는 자동 생성
    @Mapping(target = "joinDate", ignore = true)  // 자동 생성
    @Mapping(target = "exitDate", ignore = true)  // 초기값 null
    @Mapping(target = "active", constant = "true")  // 기본값 true
    MemberEntity toEntity(MemberJoinRequest request);

    // Entity List → DTO List
    List<MemberResponse> toResponseList(List<MemberEntity> entities);

    // Entity List → DTO List (활성 회원만)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    List<MemberResponse> toActiveResponseList(List<MemberEntity> entities);
}
