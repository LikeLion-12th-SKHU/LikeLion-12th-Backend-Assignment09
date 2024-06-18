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

@Slf4j                  // 로그를 작성하기 위한 LOMBOK 어노테이션
@RestControllerAdvice   // REST API 컨트롤러에 대한 예외처리 어드바이스임을 나타내는 어노테이션
public class ControllerAdvice {

    // custom error
    @ExceptionHandler({ // 클래스의 모든 하위 예외를 처리한다.
            InvalidMemberException.class,           // 회원 정보가 맞지 않을 때 발생하는 예외
            InvalidNickNameAddressException.class   // 닉네임 형식이 옳지 않을 때 발생하는 예외
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        // ErrorResponse 객체를 생성하여 HTTP 상태코드(500)와 응답 메시지를 받아 errorResponse 에 저장한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        log.error(e.getMessage());                  // 에러 형태의 응답 메시지를 콘솔에 출력한다.

        // HTTP 상태코드(500)와 응답 데이터를 제네릭 형태로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ // 클래스의 모든 하위 예외를 처리한다. 상동
            NotFoundMemberException.class,          // 회원을 찾을 수 없을 때 발생하는 예외
            NotFoundPostException.class             // 게시물을 찾을 수 없을 때 발생하는 예외
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        // ErrorResponse 객체를 생성하여 HTTP 상태코드(404)와 응답 메시지를 받아 errorResponse 에 저장한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        log.error(e.getMessage());                  // 에러 형태의 응답 메시지를 콘솔에 출력한다.

        // HTTP 상태코드(404)와 응답 데이터를 제네릭 형태로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Validation 관련 예외 처리, 클래스의 모든 하위 예외를 처리한다. 상동
    @ExceptionHandler(MethodArgumentNotValidException.class) // Controller 에 입력된 값이 @valid 와 안맞을 때 발생하는 예외
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        // Object 가 null 인지 아닌지를 검사하고 NullPointer 가 있으면 사용자 정의된 getFieldError()를 fieldError 에 저장한다.
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        // ErrorResponse 객체를 생성하여 HTTP 상태코드(400)와 응답 메시지를 errorResponse 에 저장한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));

        // 에러 형태의 응답 메시지를 콘솔에 출력한다.
        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        // HTTP 상태코드(400)와 응답 데이터를 제네릭 형태로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
