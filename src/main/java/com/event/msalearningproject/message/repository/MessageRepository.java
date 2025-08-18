package com.event.msalearningproject.message.repository;

import com.event.msalearningproject.message.repository.entity.MessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageHistory, Long> {


    List<MessageHistory> findByMemberIdAndVisibleTrueOrderBySentAtDesc(String memberId);

    List<MessageHistory> findByPhoneNumberAndVisibleTrueOrderBySentAtDesc(String phoneNumber);
}
