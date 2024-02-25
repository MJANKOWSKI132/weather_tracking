CREATE TABLE IF NOT EXISTS city (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS scheduler_run (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time_started TIMESTAMP NOT NULL,
    time_finished TIMESTAMP NULL,
    status VARCHAR(255) NOT NULL,
    additional_context VARCHAR(255) NULL,
    job_id VARCHAR(255) NULL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS city_weather (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_id BIGINT NOT NULL,
    temp DOUBLE NOT NULL,
    feels_like DOUBLE NOT NULL,
    temp_min DOUBLE NOT NULL,
    temp_max DOUBLE NOT NULL,
    pressure DOUBLE NOT NULL,
    humidity DOUBLE NOT NULL,
    main VARCHAR(255) NOT NULL,
    speed DOUBLE NOT NULL,
    time_retrieved TIMESTAMP NOT NULL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1,
    FOREIGN KEY (city_id) REFERENCES city(id)
);

CREATE TABLE IF NOT EXISTS weather_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL,
    user_id BIGINT,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS city_weather_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weather_profile_id BIGINT NOT NULL,
    city_id BIGINT NOT NULL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    version INT DEFAULT 1,
    FOREIGN KEY (weather_profile_id) REFERENCES weather_profile(id),
    FOREIGN KEY (city_id) REFERENCES city(id)
);

CREATE TABLE IF NOT EXISTS audit_log_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time_started TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_finished TIMESTAMP NULL,
    status VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NULL,
    additional_context VARCHAR(255) NULL
);

-- As an extra could build indexes