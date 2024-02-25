package com.weather.tracking.controller;

import com.weather.tracking.audit.RequestContextHolder;
import com.weather.tracking.dto.request.UserRegistrationRequestDto;
import com.weather.tracking.dto.response.UserRegistrationResponseDto;
import com.weather.tracking.exception.UserAlreadyExistsException;
import com.weather.tracking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDto registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequest) throws UserAlreadyExistsException {
        return userService.registerUser(userRegistrationRequest);
    }
}
