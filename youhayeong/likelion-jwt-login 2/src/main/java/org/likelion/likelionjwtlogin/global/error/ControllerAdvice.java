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

@Slf4j  // 로깅을 위한 로거 생성 어노테이션
@RestControllerAdvice
public class ControllerAdvice {
    // spring security를 이용해 rest api를 구현할 때 비즈니스 로직에서 발생하는 예외를 처리해 주기 위한 controller advice

    // custom error
    @ExceptionHandler({ // 여러 개의 예외를 처리해야 한다면 구체적으로 예외 명시가 필요
            InvalidMemberException.class,   // 유효한 멤버가 아닌 예외 명시
            InvalidNickNameAddressException.class   // 유효한 닉네임 주소가 아닌 예외 명시
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());    // 에러 응답 객체 생성
        log.error(e.getMessage());  // 에러 메시지를 로그에 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);     // 위에서 생성한 에러 응답 객체와 상태 코드 리턴
    }

    @ExceptionHandler({ // 예외 명시
            NotFoundMemberException.class,  // 멤버를 찾을 수 없는 예외 명시
            NotFoundPostException.class     // 게시글을 찾을 수 없는 예외 명시
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());  // 에러 응답 객체 생성
        log.error(e.getMessage());  // 에러 메시지를 로그에 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);   // 위에서 생성한 에러 응답 객체와 상태 코드 리턴
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)    // validation 예외 명시
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));  // 에러 응답 객체 생성

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());  // 로그에 기록
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 응답 객체와 상태 코드 리턴
    }
}
