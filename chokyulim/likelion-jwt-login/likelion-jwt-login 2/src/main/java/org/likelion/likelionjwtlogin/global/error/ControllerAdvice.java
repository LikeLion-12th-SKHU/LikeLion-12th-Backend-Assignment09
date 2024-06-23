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

@Slf4j // 로그를 사용하기 위한 Lombok 어노테이션
@RestControllerAdvice // 전역 예외 처리를 위한 Spring 어노테이션
public class ControllerAdvice {

    // 특정 예외에 대한 처리
    @ExceptionHandler({ // 예외를 처리하는 메서드
            InvalidMemberException.class, // 유효하지 않은 멤버 예외
            InvalidNickNameAddressException.class // 유효하지 않은 닉네임 주소 예외
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()); // 에러 응답 객체 생성
        log.error(e.getMessage()); // 에러 메시지를 로그로 출력

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 상태 코드와 에러 응답 객체 반환
    }

    // 또 다른 특정 예외에 대한 처리
    @ExceptionHandler({ // 예외를 처리하는 메서드
            NotFoundMemberException.class, // 멤버를 찾을 수 없는 예외
            NotFoundPostException.class // 게시물을 찾을 수 없는 예외
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()); // 에러 응답 객체 생성
        log.error(e.getMessage()); // 에러 메시지를 로그로 출력

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 상태 코드와 에러 응답 객체 반환
    }

    // 유효성 검사 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class) // MethodArgumentNotValidException 예외를 처리하는 메서드
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); // 필드 에러 객체를 가져옴
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())); // 에러 응답 객체 생성, 메시지와 필드 정보 포함

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage()); // 유효성 검사 에러 메시지를 로그로 출력
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 상태 코드와 에러 응답 객체 반환
    }
}
