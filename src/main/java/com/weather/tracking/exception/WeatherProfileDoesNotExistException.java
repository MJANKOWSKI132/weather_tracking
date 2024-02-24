package com.weather.tracking.exception;

public class WeatherProfileDoesNotExistException extends Exception {
    public WeatherProfileDoesNotExistException(String nickname, String parentUserEmail) {
        super(String.format("No such weather profile with nickname: %s exists for the user with email: %s", nickname, parentUserEmail));
    }

    public WeatherProfileDoesNotExistException(Long id) {
        super(String.format("No such weather profile with ID: %s exists", id));
    }
}
