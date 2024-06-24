package org.likelion.likelionjwtlogin.post.api.dto;

import jakarta.validation.Valid;
import org.likelion.likelionjwtlogin.global.template.RspTemplate;
import org.likelion.likelionjwtlogin.post.api.dto.request.PostSaveReqDto;
import org.likelion.likelionjwtlogin.post.api.dto.response.PostListResDto;
import org.likelion.likelionjwtlogin.post.application.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping()
    public RspTemplate<String> postSave(@AuthenticationPrincipal String email,  // jwt~에서 저장하면 email 자동으로 받아짐(사용자 정보),
                                        @RequestBody @Valid PostSaveReqDto postSaveReqDto) {
        postService.postSave(email, postSaveReqDto);
        return new RspTemplate<>(HttpStatus.CREATED, "게시물 생성");
    }

    @GetMapping()
    public ResponseEntity<PostListResDto> myPostFindAll(@AuthenticationPrincipal String email) {
        PostListResDto postListResDto = postService.postFindMember(email);
        return new ResponseEntity<>(postListResDto, HttpStatus.OK);
    }

}
