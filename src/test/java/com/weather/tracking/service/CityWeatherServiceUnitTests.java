package com.weather.tracking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CityWeatherServiceUnitTests {
    @Mock
    private CityRepository cityRepository;
    @Mock
    private OpenWeatherClient openWeatherClient;
    @Mock
    private CityWeatherRepository cityWeatherRepository;
    @InjectMocks
    private CityWeatherService service;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @BeforeAll
    static void setup() {

    }
    @Test
    public void testPollCityInformation() throws IOException {
        City melbourneCity = new City("melbourne");
        City sydneyCity = new City("sydney");
        List<City> allCities = List.of(melbourneCity, sydneyCity);

        doReturn(allCities).when(cityRepository).findAll();

        Path melbournePath = Path.of(ResourceUtils.getFile("classpath:melbourne-open-weather-response.json").toURI());
        Path sydneyPath = Path.of(ResourceUtils.getFile("classpath:sydney-open-weather-response.json").toURI());

        String melbourneResponseStr = Files.readString(melbournePath);
        String sydneyResponseStr = Files.readString(sydneyPath);

        OpenWeatherCityWeatherResponseDto melbourneResponse = objectMapper.readValue(melbourneResponseStr, OpenWeatherCityWeatherResponseDto.class);
        OpenWeatherCityWeatherResponseDto sydneyResponse = objectMapper.readValue(sydneyResponseStr, OpenWeatherCityWeatherResponseDto.class);

        doReturn(melbourneResponse).when(openWeatherClient).getWeatherInformation(eq("melbourne"), anyString(), any());
        doReturn(sydneyResponse).when(openWeatherClient).getWeatherInformation(eq("sydney"), anyString(), any());

        long numberOfCityWeathersModified = assertDoesNotThrow(() -> service.pollCityWeatherInformation());

        assertThat(numberOfCityWeathersModified).isEqualTo(allCities.size());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<CityWeather>> cityWeatherListCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(cityWeatherRepository).saveAll(cityWeatherListCaptor.capture());

        Iterable<CityWeather> cityWeatherIterable = cityWeatherListCaptor.getValue();
        Map<String, CityWeather> cityWeatherMap = new HashMap<>();
        for (CityWeather cityWeather : cityWeatherIterable) {
            cityWeatherMap.put(cityWeather.getCity().getName(), cityWeather);
        }

        assertThat(cityWeatherMap).containsKey("melbourne");
        assertThat(cityWeatherMap).containsKey("sydney");

        CityWeather melbourneCityWeather = cityWeatherMap.get("melbourne");
        CityWeather sydneyCityWeather = cityWeatherMap.get("sydney");

        assertThat(melbourneCityWeather)
                .usingRecursiveComparison()
                .ignoringFields("id", "city", "timeRetrieved", "timeCreated", "lastModified", "version", "weather")
                .isEqualTo(melbourneResponse);
        assertThat(melbourneCityWeather.getWeather()).usingRecursiveComparison().isEqualTo(melbourneResponse.getWeatherList().get(0));

        assertThat(sydneyCityWeather)
                .usingRecursiveComparison()
                .ignoringFields("id", "city", "timeRetrieved", "timeCreated", "lastModified", "version", "weather")
                .isEqualTo(sydneyResponse);
        assertThat(sydneyCityWeather.getWeather()).usingRecursiveComparison().isEqualTo(sydneyResponse.getWeatherList().get(0));
    }
}
