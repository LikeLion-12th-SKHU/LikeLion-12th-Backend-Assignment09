package org.likelion.likelionjwtlogin.member.domain.repository;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.likelion.likelionjwtlogin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);
}
