package vn.edu.sba301.slot24.auth;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("EMAIL_EXISTS");
    }
}
