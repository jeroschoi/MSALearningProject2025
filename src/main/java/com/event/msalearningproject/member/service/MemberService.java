package com.event.msalearningproject.member.service;

import com.event.msalearningproject.member.dto.MemberJoinRequest;
import com.event.msalearningproject.member.dto.MemberResponse;
import com.event.msalearningproject.member.repository.entity.MemberEntity;
import com.event.msalearningproject.member.exception.MemberErrorCode;
import com.event.msalearningproject.member.exception.MemberException;
import com.event.msalearningproject.member.mapper.MemberMapper;
import com.event.msalearningproject.member.repository.MemberRepository;
import com.event.msalearningproject.message.dto.MessageRequestDto;
import com.event.msalearningproject.message.entity.MessageType;
import com.event.msalearningproject.message.service.MessageSendService;
import com.event.msalearningproject.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MessageService messageService;
    private final MessageSendService messageSendService;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse join(MemberJoinRequest request) {
        try {
            validateDuplicateMember(request);
            MemberEntity memberEntity = createMemberEntity(request);
            MemberEntity savedMemberEntity = saveMember(memberEntity);
            sendJoinMessage(savedMemberEntity);
            return memberMapper.toResponse(savedMemberEntity);
            
        } catch (DataIntegrityViolationException e) {
            log.error("회원가입 DB 오류: {} - {}", request.getUserId(), e.getMessage());
            throw new MemberException(MemberErrorCode.DATABASE_ERROR, "DB 오류");
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원가입 중 예상치 못한 오류: {} - {}", request.getUserId(), e.getMessage());
            throw new MemberException(MemberErrorCode.INTERNAL_SERVER_ERROR, "예상치 못한 오류.");
        }
    }

    private MemberEntity createMemberEntity(MemberJoinRequest request) {
        MemberEntity memberEntity = memberMapper.toEntity(request);
        memberEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        return memberEntity;
    }

    private MemberEntity saveMember(MemberEntity memberEntity) {
        return memberRepository.save(memberEntity);
    }

    private void sendJoinMessage(MemberEntity memberEntity) {
        try {
            messageSendService.sendMessage(
                    MessageRequestDto.builder()
                            .memberId(memberEntity.getUserId())
                            .messageType(MessageType.EMAIL)
                            .content("환영합니다! " + memberEntity.getName() + "님, 회원가입이 완료되었습니다.")
                            .build()
            );
        } catch (Exception e) {
            log.error("회원가입 메시지 전송 실패: {} - {}", memberEntity.getUserId(), e.getMessage());
        }
    }

    private void validateDuplicateMember(MemberJoinRequest request) {
        // userId
        if (memberRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_USER_ID, 
                "이미 사용 중인 사용자 ID입니다: " + request.getUserId());
        }
        
        // email
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL, 
                "이미 사용 중인 이메일입니다: " + request.getEmail());
        }
        
        // contact
        if (memberRepository.findByContact(request.getContact()).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_CONTACT, 
                "이미 사용 중인 연락처입니다: " + request.getContact());
        }
    }

    @Transactional
    public void exit(String userId) {
        try {
            MemberEntity memberEntity = findAndValidateMember(userId);
            deactivateMember(memberEntity);
            messageService.visableFalseMessageHistory(memberEntity.getUserId());
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원탈퇴 중 예상치 못한 오류: {} - {}", userId, e.getMessage());
            throw new MemberException(MemberErrorCode.INTERNAL_SERVER_ERROR, "예상치 못한 오류.");
        }
    }

    private MemberEntity findAndValidateMember(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, 
                    "존재하지 않는 사용자입니다: " + userId));

        if (!memberEntity.isActive()) {
            throw new MemberException(MemberErrorCode.ALREADY_EXITED, 
                "이미 탈퇴한 사용자입니다: " + userId);
        }
        
        return memberEntity;
    }

    private MemberEntity deactivateMember(MemberEntity memberEntity) {
        memberEntity.setActive(false);
        memberEntity.setExitDate(LocalDateTime.now());
        return memberRepository.save(memberEntity);
    }

    @Transactional(readOnly = true)
    public MemberResponse findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(memberMapper::toResponse)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, 
                    "존재하지 않는 사용자입니다: " + userId));
    }

    @Transactional(readOnly = true)
    public MemberResponse findByContact(String contact) {
        return memberRepository.findByContact(contact)
                .map(memberMapper::toResponse)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, 
                    "존재하지 않는 연락처입니다: " + contact));
    }

    @Transactional(readOnly = true)
    public Optional<MemberResponse> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(memberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllActiveMembers() {
        List<MemberEntity> activeMembers = memberRepository.findByActiveTrue();
        return memberMapper.toActiveResponseList(activeMembers);
    }
}
