// 비즈니스 로직을 수행하기 위한 객체, 사용자 클래스(현재는 엔티티)

package org.likelion.likelionjwtlogin.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.likelion.likelionjwtlogin.member.exception.InvalidMemberException;
import org.likelion.likelionjwtlogin.member.exception.InvalidNickNameAddressException;
import org.likelion.likelionjwtlogin.post.domain.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity                         // 이 클래스가 엔티티임을 알린다. JPA 에서 정의된 필드들을 바탕으로 DB에 테이블을 만든다.
@Getter                         // GET 메서드를 사용 가능하게 하는 어노테이션
// 아무 파라미터가 없는 기본 생성자를 만든다.(기본 생성자를 protected 로 만들어 준다.) / 보안상의 이유로 다른 패키지에서 생성을 못하기 위함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    // 정규 표현식을 나타내는 클래스, 닉네임에 들어갈 수 있는 문자를 제한함.
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]*$");
    // 최대 닉네임 길이를 제한하는 클래스,8자 이하로 생성가능.
    private static final int MAX_NICKNAME_LENGTH = 8;

    @Id                         // 주키, PK (Primary Key)
    // 기본키의 생성 값(전략 = 기본키 생성을 DB에 위임한다.)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id") // 매핑할 테이블의 컬럼 이름을 지정
    private Long memberId;      // 고객 ID, PK

    private String email;       // 이메일
    private String pwd;         // 비밀번호
    private String nickname;    // 닉네임

    // 열거형, Enum 타입을 클래스의 속성으로 사용할 수 있다.
    @Enumerated(EnumType.STRING)// 열거형 이름을 저장하는데 문자열 자체를 DB에 저장한다.
    private Role role;

    /*
        일 대 다(1 : N), 사용자 한명에 여러 게시물을 가질 수 있기 때문에
        일 대 관계는 관계의 종속이 되며, 그 반대편은 주인이 된다.
        mappedBy : DB에 PostList FK 컬럼을 만들지 않기 하기 위함.
        cascade = CascadeType.ALL : 부모와 자식엔티티를 한번에 영속화하며 부모객체를 삭제하면 연관된 자식 객체도 삭제.
        orphanRemoval = True : 부모 엔티티가 삭제되면 자식 엔티티도 삭제됨을 허용한다.
    */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // 엔티티 객체 생성 시 빌더 패턴을 이용하여 만들 수 있게 지정하는 어노테이션
    @Builder
    private Member(String email, String pwd, String nickname, Role role) {
        validateNickname(nickname);
        this.email = email;
        this.pwd = pwd;
        this.nickname = nickname;
        this.role = role;
    }

    private void validateNickname(String nickname) {
        Matcher matcher = NICKNAME_PATTERN.matcher(nickname);

        if (!matcher.matches()) {
            throw new InvalidNickNameAddressException();
        }
        if (nickname.isEmpty() || nickname.length() >= MAX_NICKNAME_LENGTH) {
            throw new InvalidMemberException(String.format("닉네임은 1자 이상 %d자 이하여야 합니다.", MAX_NICKNAME_LENGTH));
        }
    }
}
