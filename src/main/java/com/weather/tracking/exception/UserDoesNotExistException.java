package com.weather.tracking.exception;

public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String email) {
        super(String.format("The user with email: %s does not exist", email));
    }
}
