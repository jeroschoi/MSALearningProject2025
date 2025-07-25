package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.entity.MemberEntity;
import com.event.msalearningproject.member.repository.MemberRepository;
import com.event.msalearningproject.member.repository.MessageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MessageHistoryRepository messageHistoryRepository;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse join(MemberJoinRequest request) {

        MemberEntity memberEntity = MemberEntity.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .name(request.getName())
                .contact(request.getContact())
                .email(request.getEmail())
                .messageType(request.getMessageType())
                .active(true)
                .build();

        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);

        // 가입 메세지
        messageService.sendJoinMessage(savedMemberEntity);

        return MemberResponse.from(savedMemberEntity);
    }

    @Transactional
    public void exit(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        if (!memberEntity.isActive()) {
            throw new IllegalArgumentException("이미 탈퇴한 사용자입니다: " + userId);
        }

        // 탈퇴
        memberEntity.setActive(false);
        memberEntity.setExitDate(LocalDateTime.now());
        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);

        // 탈퇴 메세지
        messageService.sendExitMessage(savedMemberEntity);

        // 메시지 발송 이력 삭제
        messageHistoryRepository.deleteByMemberId(memberEntity.getId());
    }

    @Transactional(readOnly = true)
    public Optional<MemberResponse> findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<MemberResponse> findByContact(String contact) {
        return memberRepository.findByContact(contact)
                .map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<MemberResponse> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllActiveMembers() {
        return memberRepository.findByActiveTrue()
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }


}
