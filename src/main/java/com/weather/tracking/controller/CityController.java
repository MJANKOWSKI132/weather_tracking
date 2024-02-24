package com.weather.tracking.controller;

import com.weather.tracking.dto.response.CityResponseDto;
import com.weather.tracking.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    public CityController(final CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/supported")
    @ResponseStatus(HttpStatus.OK)
    public List<CityResponseDto> retrieveSupportedCities() {
        return cityService.retrieveSupportedCities();
    }
}
