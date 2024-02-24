package com.weather.tracking.service;

import com.weather.tracking.dto.response.CityResponseDto;
import com.weather.tracking.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(final CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponseDto> retrieveSupportedCities() {
        return cityRepository.findAll().stream()
                .map(CityResponseDto::fromEntity)
                .toList();
    }
}
