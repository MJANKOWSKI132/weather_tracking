package com.weather.tracking.dto.response;

import lombok.Data;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class ErrorResponseDto {
    private String message;
    private ZonedDateTime timestamp;
}
