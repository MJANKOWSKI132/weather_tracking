package com.weather.tracking.service;

import com.weather.tracking.audit.Auditable;
import com.weather.tracking.dto.response.CityResponseDto;
import com.weather.tracking.enums.AuditAction;
import com.weather.tracking.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(final CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Auditable(action = AuditAction.RETRIEVE_SUPPORTED_CITIES)
    public List<CityResponseDto> retrieveSupportedCities() {
        return cityRepository.findAll().stream()
                .map(CityResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
