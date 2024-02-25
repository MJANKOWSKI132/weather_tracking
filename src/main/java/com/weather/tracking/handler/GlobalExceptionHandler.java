package com.weather.tracking.handler;

import com.weather.tracking.dto.response.ErrorResponseDto;
import com.weather.tracking.exception.BaseCustomException;
import com.weather.tracking.exception.UserAlreadyExistsException;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(BaseCustomException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage(), ZonedDateTime.now()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Throwable t) {
        log.error("An exception occurred: ", t);
        return ResponseEntity.badRequest().body(new ErrorResponseDto("An unexpected error occurred", ZonedDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException validationException) {
        BindingResult result = validationException.getBindingResult();
        StringBuilder errorMessageBuilder = new StringBuilder();
        for (FieldError fieldError : result.getFieldErrors()) {
            errorMessageBuilder.append(fieldError.getDefaultMessage()).append("; ");
        }
        if (errorMessageBuilder.length() > 2)
            errorMessageBuilder.setLength(errorMessageBuilder.length() - 2); // Get rid of extra space and ';'
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errorMessageBuilder.toString(), ZonedDateTime.now()));
    }
}
