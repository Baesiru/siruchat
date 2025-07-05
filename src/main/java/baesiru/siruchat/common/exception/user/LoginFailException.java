package baesiru.siruchat.common.exception.user;


import baesiru.siruchat.common.errorcode.ErrorCode;

public class LoginFailException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public LoginFailException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public LoginFailException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public LoginFailException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public LoginFailException(ErrorCode errorCode, Throwable throwable,
                                String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
