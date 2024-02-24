package com.weather.tracking.service;

import com.weather.tracking.dto.request.WeatherProfileCreationRequestDto;
import com.weather.tracking.dto.request.WeatherProfileUpdateRequestDto;
import com.weather.tracking.dto.response.WeatherProfileCreationResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.User;
import com.weather.tracking.entity.WeatherProfile;
import com.weather.tracking.exception.UserDoesNotExistException;
import com.weather.tracking.exception.WeatherProfileAlreadyExistsException;
import com.weather.tracking.exception.WeatherProfileDoesNotExistException;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.UserRepository;
import com.weather.tracking.repository.WeatherProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
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
        weatherProfile.setCities(matchingCities);

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

        User parentUser = userRepository
                .findByEmail(parentUserEmail)
                .orElseThrow(() -> new UserDoesNotExistException(parentUserEmail));

        if (!Objects.equals(matchingWeatherProfile.getParentUser().getEmail(), parentUserEmail)) {
            //TODO: throw unauth exception
        }

        if (weatherProfileRepository.existsByNicknameAndParentUser(nicknameToChangeTo, parentUser)) {
            // TODO: throw conflict exception
        }


        Set<City> matchingCitiesToChangeTo = cityRepository.findAllByNameIn(cityNamesToChangeTo);
        if (CollectionUtils.isEmpty(matchingCitiesToChangeTo)) {
            // TODO: throw exception here
        }

        matchingWeatherProfile.setNickname(nicknameToChangeTo);
        matchingWeatherProfile.setCities(matchingCitiesToChangeTo);

        weatherProfileRepository.save(matchingWeatherProfile);
    }
}
