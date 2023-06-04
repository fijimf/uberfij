package com.fijimf.deepfij.services.user;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super("A user with email '" + email + "' already exists.");
    }
}

