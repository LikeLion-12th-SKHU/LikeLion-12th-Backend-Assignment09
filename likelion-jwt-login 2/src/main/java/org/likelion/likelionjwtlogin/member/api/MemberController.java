package org.likelion.likelionjwtlogin.member.api;

import org.likelion.likelionjwtlogin.global.template.RspTemplate;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberSaveReqDto;
import org.likelion.likelionjwtlogin.member.api.dto.response.MemberLoginResDto;
import org.likelion.likelionjwtlogin.member.application.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	@PostMapping()
	public RspTemplate<String> join(@RequestBody @Valid MemberSaveReqDto memberSaveReqDto){
		memberService.join(memberSaveReqDto);
		return new RspTemplate<>(HttpStatus.CREATED, "회원가입");
	}

	@PostMapping("/login")
	public RspTemplate<MemberLoginResDto> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto){
		return new RspTemplate<>(HttpStatus.ACCEPTED, "로그인",memberService.login(memberLoginReqDto));
	}

}
