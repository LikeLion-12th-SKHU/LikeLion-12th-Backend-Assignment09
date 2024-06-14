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

// 로그를 작성할 수 있는 로그 객체를 자동으로 생성한다.
@Slf4j
// 컨트롤러 전반에서 발생하는 예외를 처리할 수 있는 클래스를 정의한다.
@RestControllerAdvice
public class ControllerAdvice {

    // custom error 예외 처리
    @ExceptionHandler({
            InvalidMemberException.class,
            InvalidNickNameAddressException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        // 예외 정보를 바탕으로 ErrorResponse 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        // 예외 메시지를 로그에 남긴다.
        log.error(e.getMessage());

        // ErrorResponse 객체와 HTTP 상태 코드를 ResponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // NotFound 예외 처리
    @ExceptionHandler({
            NotFoundMemberException.class,
            NotFoundPostException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        // 예외 정보를 바탕으로 ErrorResponse 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        // 예외 메시지를 로그에 남긴다.
        log.error(e.getMessage());

        // ErrorResponse 객체와 HTTP 상태 코드를 ResponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        // 예외 객체에서 필드 에러 정보를 추출한다.
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        // 필드 에러 정보를 바탕으로 ErrorResponse 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));

        // 필드 에러 정보를 로그에 남긴다.
        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        // ErrorResponse 객체와 HTTP 상태 코드를 ResponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
