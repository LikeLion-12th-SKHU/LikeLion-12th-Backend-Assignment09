package org.likelion.likelionjwtlogin.global.error;

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

import java.util.Objects;

@Slf4j // 로그 사용
@RestControllerAdvice // 전역 예외 처리
public class ControllerAdvice {

    // custom error
    @ExceptionHandler({ // 예외 처리
            InvalidMemberException.class,
            InvalidNickNameAddressException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) { // 예외 처리 메서드
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()); // 에러 응답 생성
        log.error(e.getMessage()); // 에러 메세지 로그 출력

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 상태 코드, 에러 응답 반환
    }

    @ExceptionHandler({ // 예외  처리
            NotFoundMemberException.class,
            NotFoundPostException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) { // 예외 처리 메서드
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()); // 에러 응답 생성
        log.error(e.getMessage()); // 에러 메세지 로그 출력

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 상태 코드, 에러 응답 반환
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class) // 예외 처리
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) { // 예외 처리 메서드
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); // 필드 에러 객체 가져옴
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())); // 에러 응답 생성

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage()); // 에러 메세지 로그 출력
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 상태 코드, 에러 응답 반환
    }
}
