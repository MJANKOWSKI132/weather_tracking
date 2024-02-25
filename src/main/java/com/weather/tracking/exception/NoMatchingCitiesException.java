package com.weather.tracking.exception;

import java.util.List;
import java.util.Set;

public class NoMatchingCitiesException extends BaseCustomException {
    public NoMatchingCitiesException(Set<String> cityNames) {
        super(String.format("None of the cities: [%s] are supported by the Weather Tracking API", String.join(", ", cityNames)));
    }
}
