package com.example.lab06jwt.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("email_already_registered");
    }
}
