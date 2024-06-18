// 엔티티에 의해 생성된 DB 테이블에 접근하는 메서드들을 사용하기 위한 인터페이스.

package org.likelion.likelionjwtlogin.post.domain.repository;

import java.util.List;

import org.likelion.likelionjwtlogin.member.domain.Member;
import org.likelion.likelionjwtlogin.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository 를 상속 할 때 제너릭스 타입으로 < 엔티티의 타입, 엔티티 PK의 속성타입 >을 지정한다.
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);

}
