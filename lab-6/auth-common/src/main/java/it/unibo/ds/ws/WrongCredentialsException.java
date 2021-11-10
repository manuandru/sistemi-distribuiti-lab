package it.unibo.ds.ws;

public class WrongCredentialsException extends Exception {
    public WrongCredentialsException() {
    }

    public WrongCredentialsException(String message) {
        super(message);
    }

    public WrongCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongCredentialsException(Throwable cause) {
        super(cause);
    }
}
