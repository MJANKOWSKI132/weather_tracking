package com.weather.tracking.service;

import com.weather.tracking.audit.Auditable;
import com.weather.tracking.dto.request.DeleteWeatherProfileRequestDto;
import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.dto.request.WeatherProfileUpdateRequestDto;
import com.weather.tracking.dto.response.CityWeatherProfileResponseDto;
import com.weather.tracking.dto.response.WeatherProfileCreationResponseDto;
import com.weather.tracking.dto.response.WeatherProfileResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.entity.CityWeatherProfile;
import com.weather.tracking.entity.User;
import com.weather.tracking.entity.WeatherProfile;
import com.weather.tracking.enums.AuditAction;
import com.weather.tracking.exception.NoMatchingCitiesException;
import com.weather.tracking.exception.UnauthorizedException;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherProfileRepository;
import com.weather.tracking.repository.UserRepository;
import com.weather.tracking.repository.WeatherProfileRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherProfileService {
    private final WeatherProfileRepository weatherProfileRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;

    public WeatherProfileService(final WeatherProfileRepository weatherProfileRepository,
                                 final UserRepository userRepository,
                                 final CityRepository cityRepository) {
        this.weatherProfileRepository = weatherProfileRepository;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional
    @Auditable(action = AuditAction.CREATE_WEATHER_PROFILE)
    public WeatherProfileCreationResponseDto createWeatherProfile(WeatherProfileCreationRequestDto weatherProfileCreationRequest) throws WeatherProfileAlreadyExistsException, UserDoesNotExistException, NoMatchingCitiesException {
        String nickname = weatherProfileCreationRequest.getNickname();

        String parentUserEmail = weatherProfileCreationRequest.getUserEmail();

        User parentUser = userRepository
                .findByEmail(parentUserEmail)
                .orElseThrow(() -> new UserDoesNotExistException(parentUserEmail));

        if (weatherProfileRepository.existsByNicknameAndParentUser(nickname, parentUser))
            throw new WeatherProfileAlreadyExistsException(nickname, parentUser.getEmail());

        Set<String> caseInsensitiveCityNames = weatherProfileCreationRequest.getCityNames()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<City> matchingCities = cityRepository.findAllByNameIn(caseInsensitiveCityNames);
        if (CollectionUtils.isEmpty(matchingCities))
            throw new NoMatchingCitiesException(caseInsensitiveCityNames);
        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setNickname(nickname);
        weatherProfile.setParentUser(parentUser);

        for (City city : matchingCities) {
            CityWeatherProfile cityWeatherProfile = new CityWeatherProfile();
            cityWeatherProfile.setWeatherProfile(weatherProfile);
            cityWeatherProfile.setCity(city);
            weatherProfile.getCityWeatherProfiles().add(cityWeatherProfile);
        }

        weatherProfileRepository.save(weatherProfile);

        return new WeatherProfileCreationResponseDto(weatherProfile.getId());
    }

    @Auditable(action = AuditAction.UPDATE_WEATHER_PROFILE)
    @Transactional
    public void updateWeatherProfile(WeatherProfileUpdateRequestDto weatherProfileUpdateRequest) throws NoMatchingCitiesException, UnauthorizedException, WeatherProfileDoesNotExistException, WeatherProfileAlreadyExistsException, UserDoesNotExistException {
        Long weatherProfileId = weatherProfileUpdateRequest.getId();
        String parentUserEmail = weatherProfileUpdateRequest.getUserEmail();

        if (!userRepository.existsByEmail(parentUserEmail))
            throw new UserDoesNotExistException(parentUserEmail);

        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(weatherProfileId)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(weatherProfileId));

        String nicknameToChangeTo = weatherProfileUpdateRequest.getNickname();
        Set<String> caseInsensitiveCityNamesToChangeTo = weatherProfileUpdateRequest.getCityNames()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), parentUserEmail))
            throw new UnauthorizedException(String.format("The Weather Profile with ID: %s was not created by you", weatherProfileId));

        Optional<WeatherProfile> matchingWeatherProfileWithSameNicknameAndUser = weatherProfileRepository.findByNicknameAndParentUserEmail(nicknameToChangeTo, parentUserEmail);
        if (matchingWeatherProfileWithSameNicknameAndUser.isPresent() && !Objects.equals(matchingWeatherProfileWithSameNicknameAndUser.get().getId(), matchingWeatherProfile.getId()))
            throw new WeatherProfileAlreadyExistsException(nicknameToChangeTo, parentUserEmail);

        Set<City> matchingCitiesToChangeTo = cityRepository.findAllByNameIn(caseInsensitiveCityNamesToChangeTo);
        if (CollectionUtils.isEmpty(matchingCitiesToChangeTo))
            throw new NoMatchingCitiesException(caseInsensitiveCityNamesToChangeTo);

        if (Objects.nonNull(nicknameToChangeTo))
            matchingWeatherProfile.setNickname(nicknameToChangeTo);

        matchingWeatherProfile.getCityWeatherProfiles().clear();
        for (City city : matchingCitiesToChangeTo) {
            CityWeatherProfile cityWeatherProfile = new CityWeatherProfile();
            cityWeatherProfile.setWeatherProfile(matchingWeatherProfile);
            cityWeatherProfile.setCity(city);
            matchingWeatherProfile.getCityWeatherProfiles().add(cityWeatherProfile);
        }

        matchingWeatherProfile.setVersion(matchingWeatherProfile.getVersion() + 1);
        matchingWeatherProfile.setLastModified(ZonedDateTime.now());

        weatherProfileRepository.save(matchingWeatherProfile);
    }

    @Auditable(action = AuditAction.DELETE_WEATHER_PROFILE)
    @Transactional
    public void deleteWeatherProfile(DeleteWeatherProfileRequestDto deleteWeatherProfileRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException, UnauthorizedException {
        Long weatherProfileId = deleteWeatherProfileRequest.getId();

        String parentUserEmail = deleteWeatherProfileRequest.getUserEmail();

        if (!userRepository.existsByEmail(parentUserEmail))
            throw new UserDoesNotExistException(parentUserEmail);

        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(weatherProfileId)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(weatherProfileId));

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), parentUserEmail))
            throw new UnauthorizedException(String.format("The Weather Profile with ID: %s was not created by you", deleteWeatherProfileRequest.getId()));

        weatherProfileRepository.delete(matchingWeatherProfile);
    }

    @Auditable(action = AuditAction.RETRIEVE_ALL_WEATHER_PROFILES_FOR_USER)
    @Transactional
    public List<WeatherProfileResponseDto> retrieveWeatherProfiles(String userEmail) throws UserDoesNotExistException {
        if (!userRepository.existsByEmail(userEmail))
            throw new UserDoesNotExistException(userEmail);

        List<WeatherProfile> weatherProfiles = weatherProfileRepository.findAllByParentUserEmail(userEmail);

        List<WeatherProfileResponseDto> responseList = new ArrayList<>();
        for (WeatherProfile weatherProfile : weatherProfiles) {
            WeatherProfileResponseDto responseDto = new WeatherProfileResponseDto();
            for (City city : weatherProfile.getCityWeatherProfiles().stream().map(CityWeatherProfile::getCity).collect(Collectors.toList()))
                responseDto.getCityWeatherProfiles().add(CityWeatherProfileResponseDto.fromEntity(city));
            responseDto.setNickname(weatherProfile.getNickname());
            responseDto.setId(weatherProfile.getId());
            responseList.add(responseDto);
        }
        return responseList;
    }

    @Auditable(action = AuditAction.RETRIEVE_SPECIFIC_WEATHER_PROFILE)
    @Transactional
    public WeatherProfileResponseDto retrieveWeatherProfile(Long id, String userEmail) throws WeatherProfileDoesNotExistException, UserDoesNotExistException, UnauthorizedException {
        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(id)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(id));

        if (!userRepository.existsByEmail(userEmail))
            throw new UserDoesNotExistException(userEmail);

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), userEmail))
            throw new UnauthorizedException(String.format("The Weather Profile with ID: %s was not created by you", id));

        WeatherProfileResponseDto response = new WeatherProfileResponseDto();
        for (City city : matchingWeatherProfile.getCityWeatherProfiles().stream().map(CityWeatherProfile::getCity).collect(Collectors.toList()))
            response.getCityWeatherProfiles().add(CityWeatherProfileResponseDto.fromEntity(city));
        response.setNickname(matchingWeatherProfile.getNickname());
        response.setId(matchingWeatherProfile.getId());

        return response;
    }
}
