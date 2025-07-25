package com.event.msalearningproject.member.repository;

import com.event.msalearningproject.member.entity.MessageHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistoryEntity, Long> {

    List<MessageHistoryEntity> findByMemberId(Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MessageHistoryEntity mh WHERE mh.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
