package org.likelion.likelionjwtlogin.global.error;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.likelion.likelionjwtlogin.global.error.dto.ErrorResponse;
import org.likelion.likelionjwtlogin.member.exception.InvalidMemberException;
import org.likelion.likelionjwtlogin.member.exception.InvalidNickNameAddressException;
import org.likelion.likelionjwtlogin.member.exception.NotFoundMemberException;
import org.likelion.likelionjwtlogin.post.exception.NotFoundPostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j // 로그 기능을 제공
@RestControllerAdvice // 컨트롤러에서 발생하는 예외를 처리
public class ControllerAdvice {

    // custom error
    @ExceptionHandler({ // 사용자 정의 예외 처리 메서드
            InvalidMemberException.class, // 유효하지 않은 멤버 예외
            InvalidNickNameAddressException.class // 유효하지 않은 닉네임 예외
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()); // 에러 응답 객체 생성
        log.error(e.getMessage()); // 에러 메시지를 로그로 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 에러 응답 객체 반환
    }
    // custom error
    @ExceptionHandler({ //
            NotFoundMemberException.class, // 멤버를 찾을 수 없는 예외
            NotFoundPostException.class // 게시글을 찾을 수 없는 예외
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()); // 에러 응답 객체 생성
        log.error(e.getMessage()); // 에러 메시지를 로그로 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 에러 응답 객체 반환
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); // 필드 에러 가져옴
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())); // 에러 응답 객체 생성

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage()); // 유효성 검사 에러 메시지를 로그에 출력
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 에러 응답 객체 반환
    }
}
