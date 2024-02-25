package com.weather.tracking.service;

import com.weather.tracking.dto.request.DeleteWeatherProfileRequestDto;
import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.dto.request.WeatherProfileUpdateRequestDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeatherProfile;
import com.weather.tracking.entity.User;
import com.weather.tracking.entity.WeatherProfile;
import com.weather.tracking.exception.NoMatchingCitiesException;
import com.weather.tracking.exception.UnauthorizedException;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
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

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
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

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
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

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
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
                    && Objects.equals(
                            weatherProfileCreationRequest.getCityNames(),
                            weatherProfile.getCityWeatherProfiles().stream()
                                    .map(CityWeatherProfile::getCity)
                                    .map(City::getName)
                                    .collect(Collectors.toSet())
                    );
        }));
    }

    @Test
    public void testUpdateWeatherProfileUserDoesNotExist() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        doReturn(false).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());

        assertThrows(UserDoesNotExistException.class, () -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
    }

    @Test
    public void testUpdateWeatherProfileWeatherProfileDoesNotExist() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        doReturn(true).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());
        doReturn(Optional.empty()).when(weatherProfileRepository).findById(weatherProfileUpdateRequest.getId());

        assertThrows(WeatherProfileDoesNotExistException.class, () -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
    }

    @Test
    public void testUpdateWeatherProfileUserNotOwnerOfWeatherProfile() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        User parentUser = new User();
        parentUser.setEmail("michael2@email.com");
        parentUser.setName("differentParentUserThanOneRequestingUpdate");

        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setParentUser(parentUser);
        weatherProfile.setId(1L);
        weatherProfile.setNickname("originalNickname");

        doReturn(true).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());
        doReturn(Optional.of(weatherProfile)).when(weatherProfileRepository).findById(weatherProfileUpdateRequest.getId());

        assertThrows(UnauthorizedException.class, () -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
    }

    @Test
    public void testUpdateWeatherProfileConflictWithNickname() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        User parentUser = new User();
        parentUser.setEmail(weatherProfileUpdateRequest.getUserEmail());

        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setParentUser(parentUser);
        weatherProfile.setId(1L);
        weatherProfile.setNickname("originalNickname");

        doReturn(true).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());
        doReturn(Optional.of(weatherProfile)).when(weatherProfileRepository).findById(weatherProfileUpdateRequest.getId());
        doReturn(true).when(weatherProfileRepository).existsByNicknameAndParentUserEmail(weatherProfileUpdateRequest.getNickname(), parentUser.getEmail());

        assertThrows(WeatherProfileAlreadyExistsException.class, () -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
    }

    @Test
    public void testUpdateWeatherProfileNoMatchingCities() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        User parentUser = new User();
        parentUser.setEmail(weatherProfileUpdateRequest.getUserEmail());

        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setParentUser(parentUser);
        weatherProfile.setId(1L);
        weatherProfile.setNickname("originalNickname");

        doReturn(true).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());
        doReturn(Optional.of(weatherProfile)).when(weatherProfileRepository).findById(weatherProfileUpdateRequest.getId());
        doReturn(false).when(weatherProfileRepository).existsByNicknameAndParentUserEmail(weatherProfileUpdateRequest.getNickname(), parentUser.getEmail());
        doReturn(Collections.emptySet()).when(cityRepository).findAllByNameIn(weatherProfileUpdateRequest.getCityNames());

        assertThrows(NoMatchingCitiesException.class, () -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, never()).save(any(WeatherProfile.class));
    }

    @Test
    public void testUpdateWeatherProfileSuccess() {
        WeatherProfileUpdateRequestDto weatherProfileUpdateRequest = new WeatherProfileUpdateRequestDto();
        weatherProfileUpdateRequest.setUserEmail("michael@email.com");
        weatherProfileUpdateRequest.setNickname("nicknameToChangeTo");
        weatherProfileUpdateRequest.setCityNames(Set.of("melbourne", "sydney"));
        weatherProfileUpdateRequest.setId(1L);

        User parentUser = new User();
        parentUser.setEmail(weatherProfileUpdateRequest.getUserEmail());

        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setParentUser(parentUser);
        weatherProfile.setId(1L);
        weatherProfile.setNickname("originalNickname");

        Set<City> citySet = Set.of(new City("melbourne"), new City("sydney"));

        doReturn(true).when(userRepository).existsByEmail(weatherProfileUpdateRequest.getUserEmail());
        doReturn(Optional.of(weatherProfile)).when(weatherProfileRepository).findById(weatherProfileUpdateRequest.getId());
        doReturn(false).when(weatherProfileRepository).existsByNicknameAndParentUserEmail(weatherProfileUpdateRequest.getNickname(), parentUser.getEmail());
        doReturn(citySet).when(cityRepository).findAllByNameIn(weatherProfileUpdateRequest.getCityNames());

        assertDoesNotThrow(() -> service.updateWeatherProfile(weatherProfileUpdateRequest));

        verify(weatherProfileRepository, atMostOnce()).save(argThat(wProfile -> {
            return Objects.equals(weatherProfileUpdateRequest.getNickname(), wProfile.getNickname())
                    && Objects.equals(weatherProfileUpdateRequest.getUserEmail(), wProfile.getParentUser().getEmail())
                    && Objects.equals(
                    weatherProfileUpdateRequest.getCityNames(),
                    wProfile.getCityWeatherProfiles().stream()
                            .map(CityWeatherProfile::getCity)
                            .map(City::getName)
                            .collect(Collectors.toSet())
            );
        }));
    }

    @Test
    public void testDeleteWeatherProfileUserDoesNotExist() {
        DeleteWeatherProfileRequestDto deleteWeatherProfileRequest = new DeleteWeatherProfileRequestDto();
        deleteWeatherProfileRequest.setId(1L);
        deleteWeatherProfileRequest.setUserEmail("michael@email.com");

        doReturn(false).when(userRepository).existsByEmail(deleteWeatherProfileRequest.getUserEmail());

        assertThrows(UserDoesNotExistException.class, () -> service.deleteWeatherProfile(deleteWeatherProfileRequest));

        verify(weatherProfileRepository, never()).delete(any(WeatherProfile.class));
    }

    @Test
    public void testDeleteWeatherProfileWeatherProfileDoesNotExist() {
        DeleteWeatherProfileRequestDto deleteWeatherProfileRequest = new DeleteWeatherProfileRequestDto();
        deleteWeatherProfileRequest.setId(1L);
        deleteWeatherProfileRequest.setUserEmail("michael@email.com");

        doReturn(true).when(userRepository).existsByEmail(deleteWeatherProfileRequest.getUserEmail());
        doReturn(Optional.empty()).when(weatherProfileRepository).findById(deleteWeatherProfileRequest.getId());

        assertThrows(WeatherProfileDoesNotExistException.class, () -> service.deleteWeatherProfile(deleteWeatherProfileRequest));

        verify(weatherProfileRepository, never()).delete(any(WeatherProfile.class));
    }

    @Test
    public void testDeleteWeatherProfileUserDoesNotOwnTheWeatherProfile() {
        DeleteWeatherProfileRequestDto deleteWeatherProfileRequest = new DeleteWeatherProfileRequestDto();
        deleteWeatherProfileRequest.setId(1L);
        deleteWeatherProfileRequest.setUserEmail("michael@email.com");

        User parentUser = new User();
        parentUser.setEmail("michael2@email.com");

        WeatherProfile matchingWeatherProfile = new WeatherProfile();
        matchingWeatherProfile.setId(1L);
        matchingWeatherProfile.setParentUser(parentUser);

        doReturn(true).when(userRepository).existsByEmail(deleteWeatherProfileRequest.getUserEmail());
        doReturn(Optional.of(matchingWeatherProfile)).when(weatherProfileRepository).findById(deleteWeatherProfileRequest.getId());

        assertThrows(UnauthorizedException.class, () -> service.deleteWeatherProfile(deleteWeatherProfileRequest));

        verify(weatherProfileRepository, never()).delete(any(WeatherProfile.class));
    }

    @Test
    public void testDeleteWeatherProfileSuccess() {
        DeleteWeatherProfileRequestDto deleteWeatherProfileRequest = new DeleteWeatherProfileRequestDto();
        deleteWeatherProfileRequest.setId(1L);
        deleteWeatherProfileRequest.setUserEmail("michael@email.com");

        User parentUser = new User();
        parentUser.setEmail(deleteWeatherProfileRequest.getUserEmail());

        WeatherProfile matchingWeatherProfile = new WeatherProfile();
        matchingWeatherProfile.setId(1L);
        matchingWeatherProfile.setParentUser(parentUser);

        doReturn(true).when(userRepository).existsByEmail(deleteWeatherProfileRequest.getUserEmail());
        doReturn(Optional.of(matchingWeatherProfile)).when(weatherProfileRepository).findById(deleteWeatherProfileRequest.getId());

        assertDoesNotThrow(() -> service.deleteWeatherProfile(deleteWeatherProfileRequest));

        verify(weatherProfileRepository, atMostOnce()).save(argThat(wProfile -> {
            return Objects.equals(deleteWeatherProfileRequest.getUserEmail(), wProfile.getParentUser().getEmail())
                    && Objects.equals(deleteWeatherProfileRequest.getId(), wProfile.getId());
        }));
    }
}
