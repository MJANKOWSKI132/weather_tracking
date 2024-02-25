package com.weather.tracking.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeatherProfileResponseDto {
    private Long id;
    private String nickname;
    private List<CityWeatherProfileResponseDto> cityWeatherProfiles = new ArrayList<>();
}
