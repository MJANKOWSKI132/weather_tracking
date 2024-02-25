package com.weather.tracking.controller;

import com.weather.tracking.dto.request.DeleteWeatherProfileRequestDto;
import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.dto.request.WeatherProfileUpdateRequestDto;
import com.weather.tracking.dto.response.WeatherProfileCreationResponseDto;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
import com.weather.tracking.service.WeatherProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather-profile")
public class WeatherProfileController {
    private final WeatherProfileService weatherProfileService;

    public WeatherProfileController(final WeatherProfileService weatherProfileService) {
        this.weatherProfileService = weatherProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public WeatherProfileCreationResponseDto createWeatherProfile(@RequestBody WeatherProfileCreationRequestDto weatherProfileCreationRequest) throws WeatherProfileAlreadyExistsException, UserDoesNotExistException {
        return weatherProfileService.createWeatherProfile(weatherProfileCreationRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateWeatherProfile(@RequestBody WeatherProfileUpdateRequestDto weatherProfileUpdateRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        weatherProfileService.updateWeatherProfile(weatherProfileUpdateRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeatherProfile(@RequestBody DeleteWeatherProfileRequestDto deleteWeatherProfileRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        weatherProfileService.deleteWeatherProfile(deleteWeatherProfileRequest);
    }
}
