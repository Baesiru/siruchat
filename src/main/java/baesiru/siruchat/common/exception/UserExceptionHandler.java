package baesiru.siruchat.common.exception;

import baesiru.siruchat.common.api.Api;
import baesiru.siruchat.common.errorcode.UserErrorCode;
import baesiru.siruchat.common.exception.user.UsernameExistsException;
import baesiru.siruchat.common.exception.user.LoginFailException;
import baesiru.siruchat.common.exception.user.NicknameExistsException;
import baesiru.siruchat.common.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Api<Object>> userNotFoundException(UserNotFoundException e) {
        log.info("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Api.ERROR(UserErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(value = NicknameExistsException.class)
    public ResponseEntity<Api<Object>> existNicknameException(NicknameExistsException e) {
        log.info("", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Api.ERROR(UserErrorCode.EXISTS_USER_NICKNAME));
    }

    @ExceptionHandler(value = UsernameExistsException.class)
    public ResponseEntity<Api<Object>> existUsernameException(UsernameExistsException e) {
        log.info("", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Api.ERROR(UserErrorCode.EXISTS_USERNAME));
    }

    @ExceptionHandler(value = LoginFailException.class)
    public ResponseEntity<Api<Object>> loginFailException(LoginFailException e) {
        log.info("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Api.ERROR(UserErrorCode.LOGIN_FAIL));
    }

}
