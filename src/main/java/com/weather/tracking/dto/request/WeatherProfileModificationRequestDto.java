package com.weather.tracking.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public abstract class WeatherProfileModificationRequestDto {
    private String nickname;
    private Set<String> cityNames;
    private String userEmail;
}
