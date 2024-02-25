package com.weather.tracking.service;

import com.weather.tracking.dto.response.CityResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CityServiceUnitTests {
    @Mock
    private CityRepository cityRepository;
    @InjectMocks
    private CityService service;

    @Test
    public void testRetrieveSupportedCities() {
        List<City> allCities = List.of(
                new City("city1"),
                new City("city2"),
                new City("city3")
        );
        when(cityRepository.findAll()).thenReturn(allCities);

        List<CityResponseDto> cityResponseDtoList = assertDoesNotThrow(() -> service.retrieveSupportedCities());

        assertThat(cityResponseDtoList).hasSameSizeAs(allCities);
        for (CityResponseDto cityResponseDto : cityResponseDtoList)
            assertThat(allCities.stream().anyMatch(city -> Objects.equals(city.getName(), cityResponseDto.getName()))).isTrue();
    }
}
