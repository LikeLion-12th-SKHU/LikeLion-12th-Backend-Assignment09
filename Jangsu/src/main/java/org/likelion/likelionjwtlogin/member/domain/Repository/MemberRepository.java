package org.likelion.likelionjwtlogin.member.domain.Repository;

import org.likelion.likelionjwtlogin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository 를 상속 할 때 제너릭스 타입으로 < 엔티티의 타입, 엔티티 PK의 속성타입 >을 지정한다.
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}
