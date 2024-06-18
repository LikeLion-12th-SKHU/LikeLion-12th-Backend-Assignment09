// Controller 와 Repository 를 잇는 역할을 한다.

package org.likelion.likelionjwtlogin.member.appication;

import org.likelion.likelionjwtlogin.global.jwt.TokenProvider;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberSaveReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.response.MemberLoginResDto;
import org.likelion.likelionjwtlogin.member.domain.Member;
import org.likelion.likelionjwtlogin.member.domain.Repository.MemberRepository;
import org.likelion.likelionjwtlogin.member.domain.Role;
import org.likelion.likelionjwtlogin.member.exception.InvalidMemberException;
import org.likelion.likelionjwtlogin.member.exception.NotFoundMemberException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service                            // 서비스 인터페이스를 구현하는 클래스에서 사용
// 적용된 메서드는 실행 중 예외 발생 시 해당 메서드를 실행하면서 수행한 쿼리들을 모두 롤백, 정상인 경우 변경 사항을 저장한다. (읽기 전용)
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    // 회원가입
    @Transactional
    public void join(MemberSaveReqDto memberSaveReqDto) {
        // 존재하는 이메일인지 검사
        if (memberRepository.existsByEmail(memberSaveReqDto.email())) {
            throw new InvalidMemberException("이미 존재하는 이메일입니다.");
        }
        Member member = Member.builder()
                .email(memberSaveReqDto.email())
                .pwd(passwordEncoder.encode(memberSaveReqDto.pwd()))
                .nickname(memberSaveReqDto.nickname())
                .role(Role.ROLE_USER).build();

        memberRepository.save(member);
    }

    // 로그인
    public MemberLoginResDto login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.findByEmail(memberLoginReqDto.email())
                .orElseThrow(() -> new NotFoundMemberException());
        String token = tokenProvider.generateToken(member.getEmail());

        if (!passwordEncoder.matches(memberLoginReqDto.pwd(), member.getPwd())) {
            throw new InvalidMemberException("패스워드가 일치하지 않습니다.");
        }
        return MemberLoginResDto.of(member, token);
    }
}
