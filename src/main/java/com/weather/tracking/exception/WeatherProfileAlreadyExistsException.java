package com.weather.tracking.exception;

public class WeatherProfileAlreadyExistsException extends Exception {
    public WeatherProfileAlreadyExistsException(String nickname, String parentUserEmail) {
        super(String.format("A Weather Profile with the nickname: %s already exists for the user with the email: %s", nickname, parentUserEmail));
    }
}
