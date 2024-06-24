package org.likelion.likelionjwtlogin.member.domain.repository;

import org.likelion.likelionjwtlogin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);    // 이메일이 존재하는지에 대한 boolean
}
