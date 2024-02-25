package com.weather.tracking.dto.request;

import lombok.Data;

@Data
public class DeleteWeatherProfileRequestDto {
    private Long id;
    private String userEmail;
}
