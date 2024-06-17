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

@Slf4j //로깅:정보를 제공하는 일련의 기록, 레이어:계층, 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음이다
@RestControllerAdvice //전역적으로 예외를 처리한다 ControllerAdvice와 달리 ResponseBody가 붙어있다
public class ControllerAdvice {

    // 사용자정의 예외 처리
    @ExceptionHandler({
            InvalidMemberException.class,
            InvalidNickNameAddressException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) { //잘못된 데이터 예외처리
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()); //에러 응답 생성
        log.error(e.getMessage()); //예외 메세지를 로그에 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); //에러 응답을 반환
    }

    @ExceptionHandler({
            NotFoundMemberException.class,
            NotFoundPostException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) { //찾을 수 없는 데이터 예외처리 메서드
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()); //에러 응답 생성
        log.error(e.getMessage()); //예외 메세지를 로그에 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); //에러 응답을 반환
    }

    // 유효성검사 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) { //메서드 인자 유효성 검사 예외 처리 메서드
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); //필드 에러 정보를 가져옴 널값이 아니라고 보장한다
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())); //에러 응답생성

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage()); //필드 에러 메세지를 로그에 기록함
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); //에러응답을 반환함
    }
}
