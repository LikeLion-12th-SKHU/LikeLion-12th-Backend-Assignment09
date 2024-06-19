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

@Slf4j // 로그를 쉽게 사용할 수 있게 해준다.
@RestControllerAdvice // 컨트롤러에서 발생하는 예외를 처리하기 위한 클래스를 정의한다.
public class ControllerAdvice {

    // custom error 특정 커스텀 예외 처리
    @ExceptionHandler({
            InvalidMemberException.class, // 유효하지 않는 회원 에러
            InvalidNickNameAddressException.class //유효하지 않는 닉네임 에러
    })
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        // 에러 응답 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        // 에러 메세지 로그를 출력한다.
        log.error(e.getMessage());
        // BAD_REQUEST 상태 코드와 RsponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Validation 예외들을 처리하기 위한 핸들러 메서드
    @ExceptionHandler({
            NotFoundMemberException.class, // 찾을 수 없는 회원 예외
            NotFoundPostException.class // 찾을 수 없는 포스트 예외
    })
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        // ErrorResponse 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        // 에러 메세지 로그를 출력한다.
        log.error(e.getMessage());
        // ErrorResponse객체와 NOT_FOUND 상태 코드를 ResponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Validation 관련 예외 처리를 위한 핸들러 메서드
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        // 예외 객체 생성을 하고 Null값을 체크하고 필드 에러 정보를 출력한다.
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        // 에러 메세지와 필드명을 포함한 ErrorResponse 객체를 생성한다.
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
        // 에러 메세지에 대한 정보를 로그에 남긴다.
        log.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        // BAD_REQUEST 상태 코드와 ErrorRespomse를 ResponseEntity로 반환한다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
