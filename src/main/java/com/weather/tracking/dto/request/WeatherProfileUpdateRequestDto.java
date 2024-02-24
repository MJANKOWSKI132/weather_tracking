package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherProfileUpdateRequestDto extends WeatherProfileModificationRequestDto {
    private Long id;
}
