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

@Slf4j
@RestControllerAdvice //예외처리가 발생하면 프록시역할을 통해 먼저 가져와 핸들링하는 전역 컨트롤러 예외 클래스
public class ControllerAdvice {

    // bad request error 핸들링.
    @ExceptionHandler({
            InvalidMemberException.class,
            InvalidNickNameAddressException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        log.error(e.getMessage()); //로그찍기.

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ //notfound exception 예외처리.
            NotFoundMemberException.class,
            NotFoundPostException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()); //위와 맥락은 비슷하다.
        log.error(e.getMessage()); //로그찍기.

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); //response entity로 묶어서 리턴.
    }

    // Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError()); //null이면 npe발생시킴. 아니면 필드에러를 가져옴.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));

        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage()); //로그찍기.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
