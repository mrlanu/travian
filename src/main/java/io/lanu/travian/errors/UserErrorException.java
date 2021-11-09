package io.lanu.travian.errors;

public class UserErrorException extends RuntimeException{
    public UserErrorException(String message) {
        super(message);
    }

    public UserErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserErrorException(Throwable cause) {
        super(cause);
    }
}
