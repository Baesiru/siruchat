package baesiru.siruchat.common.exception.user;


import baesiru.siruchat.common.errorcode.ErrorCode;

public class UsernameExistsException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public UsernameExistsException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public UsernameExistsException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public UsernameExistsException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public UsernameExistsException(ErrorCode errorCode, Throwable throwable,
                                   String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
