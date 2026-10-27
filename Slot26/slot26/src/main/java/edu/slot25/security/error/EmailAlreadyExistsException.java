package edu.slot25.security.error;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("EMAIL_EXISTS");
    }
}
