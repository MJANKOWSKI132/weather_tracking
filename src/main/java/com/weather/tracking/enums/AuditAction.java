package com.weather.tracking.enums;

public enum AuditAction {
    REGISTER_USER("Registering a new user"),
    RETRIEVE_SUPPORTED_CITIES("Retrieve all supported cities"),
    CREATE_WEATHER_PROFILE("Create a new weather profile"),
    UPDATE_WEATHER_PROFILE("Update an existing weather profile"),
    DELETE_WEATHER_PROFILE("Delete an existing weather profile"),
    RETRIEVE_SPECIFIC_WEATHER_PROFILE("Retrieve a specific weather profile"),
    RETRIEVE_ALL_WEATHER_PROFILES_FOR_USER("Retrieve all weather profiles a user has created");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
