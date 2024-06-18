// 사용자의 요청을 받아 서비스 계층(MemberService)에 처리를 위임하고 결과를 반환함

package org.likelion.likelionjwtlogin.member.api.dto;

import jakarta.validation.Valid;
import org.likelion.likelionjwtlogin.global.template.RspTemplate;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberSaveReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.response.MemberLoginResDto;
import org.likelion.likelionjwtlogin.member.appication.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController                            // 컨트롤러를 JSON 으로 반환하는 컨트롤러로 만든다.
@RequestMapping("/members")             // URL 을 매핑하는데 사용하는 어노테이션(/---/members)
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping()                  // POST 요청을 받을 수 있게 하는 API(공란)
    public RspTemplate<String> join(@RequestBody @Valid MemberSaveReqDto memberSaveReqDto) {
        memberService.join(memberSaveReqDto);
        return new RspTemplate<>(HttpStatus.CREATED, "회원가입");
    }

    @GetMapping()                   // GET 요청을 받을 수 있게 하는 API(공란)
    public RspTemplate<MemberLoginResDto> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto) {
        MemberLoginResDto memberLoginResDto = memberService.login(memberLoginReqDto);
        return new RspTemplate<>(HttpStatus.OK, "로그인", memberLoginResDto);
    }
}
// @RequestBody : 요청 메시지의 message body 를 자바 객체로 변환하여 준다.
// @Valid : 변환한 객체를 검증하는 자바 표준 함수. 예외 발생 시 MethodArgumentNotValidException 를 호출한다.