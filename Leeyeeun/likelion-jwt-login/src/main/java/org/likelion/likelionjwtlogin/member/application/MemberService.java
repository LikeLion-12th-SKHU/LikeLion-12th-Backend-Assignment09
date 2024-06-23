package org.likelion.likelionjwtlogin.member.application;
import org.likelion.likelionjwtlogin.global.jwt.TokenProvider;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberSaveReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.response.MemberLoginResDto;
import org.likelion.likelionjwtlogin.member.domain.Member;
import org.likelion.likelionjwtlogin.member.domain.Role;
import org.likelion.likelionjwtlogin.member.domain.repository.MemberRepository;
import org.likelion.likelionjwtlogin.member.exception.InvalidMemberException;
import org.likelion.likelionjwtlogin.member.exception.NotFoundMemberException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
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
        if (memberRepository.existsByEmail(memberSaveReqDto.email())) {
            throw new InvalidMemberException("이미 존재하는 이메일입니다."); }
        Member member = Member.builder()
                .email(memberSaveReqDto.email())
                .pwd(passwordEncoder.encode(memberSaveReqDto.pwd()))
                .nickname(memberSaveReqDto.nickname())
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);
    }
    // 로그인 (성공 -> 토큰 발급)
    public MemberLoginResDto login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.findByEmail(memberLoginReqDto.email()).orElseThrow(NotFoundMemberException::new);
        String token = tokenProvider.generateToken(member.getEmail());

        if (!passwordEncoder.matches(memberLoginReqDto.pwd(), member.getPwd()))
        {
            throw new InvalidMemberException("비밀번호가 일치하지 않습니다.");
        }

        return MemberLoginResDto.of(member, token);
    }
}