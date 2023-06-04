package com.fijimf.deepfij.services.user;

public class DuplicatedUsernameException extends RuntimeException {
    public DuplicatedUsernameException(String username) {
        super("User with '" + username + "' already exists.");
    }
}

