package com.weather.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WeatherTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherTrackingApplication.class, args);
	}

}
