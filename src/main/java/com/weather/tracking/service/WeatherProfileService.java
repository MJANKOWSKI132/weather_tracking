package com.weather.tracking.service;

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
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherProfileRepository;
import com.weather.tracking.repository.UserRepository;
import com.weather.tracking.repository.WeatherProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class WeatherProfileService {
    private final WeatherProfileRepository weatherProfileRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final CityWeatherProfileRepository cityWeatherProfileRepository;

    public WeatherProfileService(final WeatherProfileRepository weatherProfileRepository,
                                 final UserRepository userRepository,
                                 final CityRepository cityRepository,
                                 final CityWeatherProfileRepository cityWeatherProfileRepository) {
        this.weatherProfileRepository = weatherProfileRepository;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.cityWeatherProfileRepository = cityWeatherProfileRepository;
    }

    @Transactional
    public WeatherProfileCreationResponseDto createWeatherProfile(WeatherProfileCreationRequestDto weatherProfileCreationRequest) throws WeatherProfileAlreadyExistsException, UserDoesNotExistException {
        String nickname = weatherProfileCreationRequest.getNickname();
        String parentUserEmail = weatherProfileCreationRequest.getUserEmail();

        User parentUser = userRepository
                .findByEmail(parentUserEmail)
                .orElseThrow(() -> new UserDoesNotExistException(parentUserEmail));

        if (weatherProfileRepository.existsByNicknameAndParentUser(nickname, parentUser))
            throw new WeatherProfileAlreadyExistsException(nickname, parentUser.getEmail());

        Set<City> matchingCities = cityRepository.findAllByNameIn(weatherProfileCreationRequest.getCityNames());
        if (CollectionUtils.isEmpty(matchingCities)) {
            // TODO: throw exception here
        }
        WeatherProfile weatherProfile = new WeatherProfile();
        weatherProfile.setNickname(nickname);
        weatherProfile.setParentUser(parentUser);

        log.info("Size of matching cities: {}", matchingCities.size());

        for (City city : matchingCities) {
            CityWeatherProfile cityWeatherProfile = new CityWeatherProfile();
            cityWeatherProfile.setWeatherProfile(weatherProfile);
            cityWeatherProfile.setCity(city);
            weatherProfile.getCityWeatherProfiles().add(cityWeatherProfile);
        }

        weatherProfileRepository.save(weatherProfile);

        return new WeatherProfileCreationResponseDto(weatherProfile.getId());
    }

    public void updateWeatherProfile(WeatherProfileUpdateRequestDto weatherProfileUpdateRequest) throws UserDoesNotExistException, WeatherProfileDoesNotExistException {
        Long weatherProfileId = weatherProfileUpdateRequest.getId();
        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(weatherProfileId)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(weatherProfileId));

        String nicknameToChangeTo = weatherProfileUpdateRequest.getNickname();
        Set<String> cityNamesToChangeTo = weatherProfileUpdateRequest.getCityNames();
        String parentUserEmail = weatherProfileUpdateRequest.getUserEmail();

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), parentUserEmail)) {
            //TODO: throw unauth exception
        }

        if (weatherProfileRepository.existsByNicknameAndParentUserEmail(nicknameToChangeTo, parentUserEmail)) {
            // TODO: throw conflict exception
        }


        Set<City> matchingCitiesToChangeTo = cityRepository.findAllByNameIn(cityNamesToChangeTo);
        if (CollectionUtils.isEmpty(matchingCitiesToChangeTo)) {
            // TODO: throw exception here
        }

        if (Objects.nonNull(nicknameToChangeTo))
            matchingWeatherProfile.setNickname(nicknameToChangeTo);

        matchingWeatherProfile.getCityWeatherProfiles().clear();
        for (City city : matchingCitiesToChangeTo) {
            CityWeatherProfile cityWeatherProfile = new CityWeatherProfile();
            cityWeatherProfile.setWeatherProfile(matchingWeatherProfile);
            cityWeatherProfile.setCity(city);
            matchingWeatherProfile.getCityWeatherProfiles().add(cityWeatherProfile);
        }

        weatherProfileRepository.save(matchingWeatherProfile);
    }

    public void deleteWeatherProfile(DeleteWeatherProfileRequestDto deleteWeatherProfileRequest) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        Long weatherProfileId = deleteWeatherProfileRequest.getId();
        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(weatherProfileId)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(weatherProfileId));

        String parentUserEmail = deleteWeatherProfileRequest.getUserEmail();

        if (!userRepository.existsByEmail(parentUserEmail))
            throw new UserDoesNotExistException(parentUserEmail);

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), parentUserEmail)) {
            //TODO: throw unauth exception
        }

        weatherProfileRepository.delete(matchingWeatherProfile);
    }

    public List<WeatherProfileResponseDto> retrieveWeatherProfiles(String userEmail) throws UserDoesNotExistException {
        if (!userRepository.existsByEmail(userEmail))
            throw new UserDoesNotExistException(userEmail);

        List<WeatherProfile> weatherProfiles = weatherProfileRepository.findAllByParentUserEmail(userEmail);

        List<WeatherProfileResponseDto> responseList = new ArrayList<>();
        for (WeatherProfile weatherProfile : weatherProfiles) {
            WeatherProfileResponseDto responseDto = new WeatherProfileResponseDto();
            for (City city : weatherProfile.getCityWeatherProfiles().stream().map(CityWeatherProfile::getCity).toList())
                responseDto.getCityWeatherProfiles().add(CityWeatherProfileResponseDto.fromEntity(city));
            responseDto.setNickname(weatherProfile.getNickname());
            responseList.add(responseDto);
        }
        return responseList;
    }

    public WeatherProfileResponseDto retrieveWeatherProfile(Long id, String userEmail) throws WeatherProfileDoesNotExistException, UserDoesNotExistException {
        WeatherProfile matchingWeatherProfile = weatherProfileRepository
                .findById(id)
                .orElseThrow(() -> new WeatherProfileDoesNotExistException(id));

        if (!userRepository.existsByEmail(userEmail))
            throw new UserDoesNotExistException(userEmail);

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), userEmail)) {
            //TODO: throw unauth exception
        }

        WeatherProfileResponseDto response = new WeatherProfileResponseDto();
        for (City city : matchingWeatherProfile.getCityWeatherProfiles().stream().map(CityWeatherProfile::getCity).toList())
            response.getCityWeatherProfiles().add(CityWeatherProfileResponseDto.fromEntity(city));
        response.setNickname(matchingWeatherProfile.getNickname());

        return response;
    }
}
