package com.event.msalearningproject.member.repository;

import com.event.msalearningproject.member.repository.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUserId(String userId);

    Optional<MemberEntity> findByContact(String contact);

    Optional<MemberEntity> findByEmail(String email);

    List<MemberEntity> findByActiveTrue();

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByContact(String contact);
}
