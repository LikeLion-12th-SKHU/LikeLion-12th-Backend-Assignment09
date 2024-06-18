// 비즈니스 로직을 수행하기 위한 객체, 게시물 클래스(현재는 엔티티)

package org.likelion.likelionjwtlogin.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.likelion.likelionjwtlogin.member.domain.Member;

@Entity                     // 이 클래스가 엔티티임을 알림. JPA 에 정의된 필드들을 바탕으로 DB에 테이블을 만든다.
@Getter                     // get 메소드를 사용할 수 있게 한다.
// 파라미터가 없는 기본 생성자를 만든다.(기본 생성자를 protected 로 만든다.) / 보안상의 이유로, 다른 패키지에서 생성을 막기 위함.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id                     // 주키, PK (Primary key)
    // 기본 키 생성을 DB에 위임한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 매핑할 테이블의 컬럼 이름을 지정한다.(이름 = "post_id)
    @Column(name = "post_id")
    private Long postId;    // 포스트 ID, PK

    private String title;   // 제목
    private String content; // 내용

    // 다 대 일(N : 1) : 게시물은 반드시 하나의 사용자가 있어야 한다.
    // 다 대 관계가 주인이 되면 반대편은 종속관계가 된다.
    @ManyToOne(fetch = FetchType.LAZY)
    // 외래키를 매핑할 때 사용(이름 = "member_id")
    @JoinColumn(name = "member_id")
    private Member member;

    // 엔티티 객체 생성 시 빌더 패턴을 이용하여 만들 수 있게 지정하는 어노테이션
    @Builder
    private Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

}
