package org.likelion.likelionjwtlogin.member.domain;

import java.util.ArrayList;
import java.util.List;

import org.likelion.likelionjwtlogin.member.exception.InvalidMemberException;
import org.likelion.likelionjwtlogin.post.domain.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	//email, pwd, nickname
	private String email;
	private String pwd;
	private String nickname;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Post> posts = new ArrayList<>();

	@Builder
	private Member(Long memberId, String email, String pwd, String nickname, Role role) {
		validateNickname(nickname);
		this.memberId = memberId;
		this.email = email;
		this.pwd = pwd;
		this.nickname = nickname;
		this.role = role;
	}

	private void validateNickname(String nickname){
		// 닉네임은 1자 이상 그리고 8자 이상으로
		if (nickname.isEmpty() || nickname.length() > 8){
			throw new InvalidMemberException(String.format("닉네임은 1자 이상 %d자 이하여야 합니다.",8));
		}
	}
}
