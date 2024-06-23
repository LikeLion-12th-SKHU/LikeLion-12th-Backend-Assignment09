package org.likelion.likelionjwtlogin.post.domain.repository;

import org.likelion.likelionjwtlogin.member.domain.Member;
import org.likelion.likelionjwtlogin.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);
}
