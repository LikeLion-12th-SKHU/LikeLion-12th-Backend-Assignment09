package org.likelion.likelionjwtlogin.member.api;


import jakarta.validation.Valid;
import org.likelion.likelionjwtlogin.global.template.RspTemplate;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberSaveReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.response.MemberLoginResDto;
import org.likelion.likelionjwtlogin.member.application.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }
    @PostMapping()
    public RspTemplate<String> join(@RequestBody @Valid MemberSaveReqDto memberSaveReqDto){
        memberService.join(memberSaveReqDto);
        return new RspTemplate<>(HttpStatus.CREATED, "회원가입");
    }

    @GetMapping()
    public RspTemplate<MemberLoginResDto> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto){
        MemberLoginResDto memberLoginResDto = memberService.login(memberLoginReqDto);
        return new RspTemplate<>(HttpStatus.OK, "로그인", memberLoginResDto);
    }
}
