package com.weather.tracking.exception;

public class UserAlreadyExistsException extends BaseCustomException {
    public UserAlreadyExistsException(String email) {
        super(String.format("The user with email: %s already exists", email));
    }
}
