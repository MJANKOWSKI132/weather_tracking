package com.weather.tracking.exception;

public abstract class BaseCustomException extends Exception {
    public BaseCustomException(String message) {
        super(message);
    }
}
