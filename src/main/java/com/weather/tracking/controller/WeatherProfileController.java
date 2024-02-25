package com.weather.tracking.controller;

import com.weather.tracking.audit.RequestContextHolder;
import com.weather.tracking.dto.request.DeleteWeatherProfileRequestDto;
import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.dto.request.WeatherProfileUpdateRequestDto;
import com.weather.tracking.dto.response.WeatherProfileCreationResponseDto;
import com.weather.tracking.dto.response.WeatherProfileResponseDto;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
import com.weather.tracking.service.WeatherProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather-profile")
public class WeatherProfileController {
    private final WeatherProfileService weatherProfileService;
    private final RequestContextHolder requestContextHolder;

    public WeatherProfileController(final WeatherProfileService weatherProfileService,
                                    final RequestContextHolder requestContextHolder) {
        this.weatherProfileService = weatherProfileService;
        this.requestContextHolder = requestContextHolder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public WeatherProfileCreationResponseDto createWeatherProfile(@RequestBody WeatherProfileCreationRequestDto weatherProfileCreationRequest) throws WeatherProfileAlreadyExistsException, UserDoesNotExistException {
        requestContextHolder.setUserEmail(weatherProfileCreationRequest.getUserEmail());
        return weatherProfileService.createWeatherProfile(weatherProfileCreationRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateWeatherProfile(@RequestBody WeatherProfileUpdateRequestDto weatherProfileUpdateRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        requestContextHolder.setUserEmail(weatherProfileUpdateRequest.getUserEmail());
        weatherProfileService.updateWeatherProfile(weatherProfileUpdateRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeatherProfile(@RequestBody DeleteWeatherProfileRequestDto deleteWeatherProfileRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        requestContextHolder.setUserEmail(deleteWeatherProfileRequest.getUserEmail());
        weatherProfileService.deleteWeatherProfile(deleteWeatherProfileRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WeatherProfileResponseDto retrieveWeatherProfile(@RequestParam String userEmail, @RequestParam Long id) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        requestContextHolder.setUserEmail(userEmail);
        return weatherProfileService.retrieveWeatherProfile(id, userEmail);
    }

    @GetMapping("/user/all")
    @ResponseStatus(HttpStatus.OK)
    public List<WeatherProfileResponseDto> retrieveAllWeatherProfilesForUser(@RequestParam String userEmail) throws UserDoesNotExistException {
        requestContextHolder.setUserEmail(userEmail);
        return weatherProfileService.retrieveWeatherProfiles(userEmail);
    }
}
