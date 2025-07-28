package com.event.msalearningproject.member.repository;

import com.event.msalearningproject.member.repository.entity.MessageHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistoryEntity, Long> {

    List<MessageHistoryEntity> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    List<MessageHistoryEntity> findBySentTrue();

    List<MessageHistoryEntity> findBySentFalse();
}
