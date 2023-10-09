package com.cse550.projectbackend.user.error;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User with id " + userId + " not found");
    }
}