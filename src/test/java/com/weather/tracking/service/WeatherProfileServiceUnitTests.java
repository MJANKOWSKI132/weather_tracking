package com.weather.tracking.service;

import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeatherProfile;
import com.weather.tracking.entity.User;
import com.weather.tracking.exception.NoMatchingCitiesException;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.UserRepository;
import com.weather.tracking.repository.WeatherProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WeatherProfileServiceUnitTests {
    @Mock
    private WeatherProfileRepository weatherProfileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CityRepository cityRepository;
    @InjectMocks
    private WeatherProfileService service;

    @Test
    public void testCreateWeatherProfileWithNonExistentUser() {
        WeatherProfileCreationRequestDto weatherProfileCreationRequest = new WeatherProfileCreationRequestDto();
        weatherProfileCreationRequest.setNickname("nickname");
        weatherProfileCreationRequest.setUserEmail("michael@email.com");
        weatherProfileCreationRequest.setCityNames(Set.of("melbourne", "sydney"));

        doReturn(Optional.empty()).when(userRepository).findByEmail(weatherProfileCreationRequest.getUserEmail());

        assertThrows(UserDoesNotExistException.class, () -> service.createWeatherProfile(weatherProfileCreationRequest));

        verify(weatherProfileRepository, never()).save(any());
    }

    @Test
    public void testCreateWeatherProfileWithNicknameThatAlreadyExistsForUser() {
        WeatherProfileCreationRequestDto weatherProfileCreationRequest = new WeatherProfileCreationRequestDto();
        weatherProfileCreationRequest.setNickname("nickname");
        weatherProfileCreationRequest.setUserEmail("michael@email.com");
        weatherProfileCreationRequest.setCityNames(Set.of("melbourne", "sydney"));

        User user = new User();
        user.setEmail(weatherProfileCreationRequest.getUserEmail());
        user.setName("mike");

        doReturn(Optional.of(user)).when(userRepository).findByEmail(weatherProfileCreationRequest.getUserEmail());
        doReturn(true).when(weatherProfileRepository).existsByNicknameAndParentUser(weatherProfileCreationRequest.getNickname(), user);

        assertThrows(WeatherProfileAlreadyExistsException.class, () -> service.createWeatherProfile(weatherProfileCreationRequest));

        verify(weatherProfileRepository, never()).save(any());
    }

    @Test
    public void testCreateWeatherProfileNoMatchingCities() {
        WeatherProfileCreationRequestDto weatherProfileCreationRequest = new WeatherProfileCreationRequestDto();
        weatherProfileCreationRequest.setNickname("nickname");
        weatherProfileCreationRequest.setUserEmail("michael@email.com");
        weatherProfileCreationRequest.setCityNames(Set.of("melbourne", "sydney"));

        User user = new User();
        user.setEmail(weatherProfileCreationRequest.getUserEmail());
        user.setName("mike");

        doReturn(Optional.of(user)).when(userRepository).findByEmail(weatherProfileCreationRequest.getUserEmail());
        doReturn(false).when(weatherProfileRepository).existsByNicknameAndParentUser(weatherProfileCreationRequest.getNickname(), user);
        doReturn(Collections.emptySet()).when(cityRepository).findAllByNameIn(weatherProfileCreationRequest.getCityNames());

        assertThrows(NoMatchingCitiesException.class, () -> service.createWeatherProfile(weatherProfileCreationRequest));

        verify(weatherProfileRepository, never()).save(any());
    }

    @Test
    public void testCreateWeatherProfileSuccess() {
        WeatherProfileCreationRequestDto weatherProfileCreationRequest = new WeatherProfileCreationRequestDto();
        weatherProfileCreationRequest.setNickname("nickname");
        weatherProfileCreationRequest.setUserEmail("michael@email.com");
        weatherProfileCreationRequest.setCityNames(Set.of("melbourne", "sydney"));

        User user = new User();
        user.setEmail(weatherProfileCreationRequest.getUserEmail());
        user.setName("mike");

        Set<City> citySet = Set.of(new City("melbourne"), new City("sydney"));

        doReturn(Optional.of(user)).when(userRepository).findByEmail(weatherProfileCreationRequest.getUserEmail());
        doReturn(false).when(weatherProfileRepository).existsByNicknameAndParentUser(weatherProfileCreationRequest.getNickname(), user);
        doReturn(citySet).when(cityRepository).findAllByNameIn(weatherProfileCreationRequest.getCityNames());

        assertDoesNotThrow(() -> service.createWeatherProfile(weatherProfileCreationRequest));

        verify(weatherProfileRepository, atMostOnce()).save(argThat(weatherProfile -> {
            return Objects.equals(weatherProfileCreationRequest.getNickname(), weatherProfile.getNickname())
                    && Objects.equals(weatherProfileCreationRequest.getUserEmail(), weatherProfile.getParentUser().getEmail())
                    && Objects.equals(weatherProfileCreationRequest.getCityNames(), weatherProfile.getCityWeatherProfiles().stream().map(CityWeatherProfile::getCity).map(City::getName).collect(Collectors.toSet()));
        }));
    }
}
