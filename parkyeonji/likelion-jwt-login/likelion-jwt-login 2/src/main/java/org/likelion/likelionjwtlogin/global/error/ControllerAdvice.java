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

@Slf4j //로그를 쉽게 사용할 수 있도록 함
@RestControllerAdvice //컨트롤러에서 발생하는 예외를 처리하기 위한 클래스 정의
public class ControllerAdvice {

    // custom error
    @ExceptionHandler({
            InvalidMemberException.class, //유효하지 않은 회원일 경우
            InvalidNickNameAddressException.class //유효하지 않은 닉네임일 경우
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()); //에러 상태 코드와 메세지 포함
        log.error(e.getMessage()); //예외 메세지를 로그로 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); //400 상태 코드로 반환
    }

    @ExceptionHandler({
            NotFoundMemberException.class, //회원을 찾을 수 없는 경우
            NotFoundPostException.class //포스트를 찾을 수 없는 경우
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        log.error(e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); //404 상태 코드로 반환
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class) //유효성 검사가 실패한 경우
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); //예외에서 필드 오류 추출
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())); //검증 오류 메세지와 필드 이름을 포함

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); //400 상태 코드로 반환
    }
}
