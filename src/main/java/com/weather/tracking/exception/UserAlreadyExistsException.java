package com.weather.tracking.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String email) {
        super(String.format("The user with email: %s already exists", email));
    }
}
