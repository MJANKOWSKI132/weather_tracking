package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherProfileCreationRequestDto extends WeatherProfileModificationRequestDto {
}
