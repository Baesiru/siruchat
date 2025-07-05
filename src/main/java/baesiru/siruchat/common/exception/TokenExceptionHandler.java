package baesiru.siruchat.common.exception;

import baesiru.siruchat.common.api.Api;
import baesiru.siruchat.common.errorcode.TokenErrorCode;
import baesiru.siruchat.common.exception.token.TokenException;
import baesiru.siruchat.common.exception.token.TokenExpiredException;
import baesiru.siruchat.common.exception.token.TokenSignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TokenExceptionHandler {
    @ExceptionHandler(value = TokenException.class)
    public ResponseEntity<Api<Object>> tokenException(TokenException e) {
        log.warn("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Api.ERROR(TokenErrorCode.TOKEN_EXCEPTION));
    }

    @ExceptionHandler(value = TokenSignatureException.class)
    public ResponseEntity<Api<Object>> tokenSignatureException(TokenSignatureException e) {
        log.warn("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Api.ERROR(TokenErrorCode.INVALID_TOKEN));
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    public ResponseEntity<Api<Object>> tokenExpiredException(TokenExpiredException e) {
        log.warn("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Api.ERROR(TokenErrorCode.EXPIRED_TOKEN));
    }

}
