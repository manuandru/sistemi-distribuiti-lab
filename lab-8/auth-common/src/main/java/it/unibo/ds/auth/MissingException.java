package it.unibo.ds.auth;

public class MissingException extends Exception {
    public MissingException() {
    }

    public MissingException(String message) {
        super(message);
    }

    public MissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingException(Throwable cause) {
        super(cause);
    }
}
