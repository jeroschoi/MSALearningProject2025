package com.event.msalearningproject.message.repository;

import com.event.msalearningproject.message.entity.MessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRespository extends JpaRepository<MessageHistory, Long> {

    List<MessageHistory> findByMemberIdOrderBySentAtDesc(String memberId);

    List<MessageHistory> findByPhoneNumberOrderBySentAtDesc(String phoneNumber);
}
