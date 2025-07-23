package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.entity.MemberEntity;
import com.event.msalearningproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    // todo
    //private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse join(MemberJoinRequest request) {

        // 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .name(request.getName())
                .contact(request.getContact())
                .messageType(request.getMessageType())
                .active(true)
                .build();

        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);

        // 메세지 발송

        return MemberResponse.from(savedMemberEntity);

    }


}
