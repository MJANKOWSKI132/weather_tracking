package com.weather.tracking.service;

import com.weather.tracking.dto.request.UserRegistrationRequestDto;
import com.weather.tracking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService service;

    @Test
    void testUniqueUserCanRegister() {
        final String uniqueEmail = "michael@email.com";
        final String name = "mike";

        UserRegistrationRequestDto registrationRequest = new UserRegistrationRequestDto();
        registrationRequest.setEmail(uniqueEmail);
        registrationRequest.setName(name);

        doReturn(false).when(userRepository).existsByEmail(registrationRequest.getEmail());

        assertDoesNotThrow(() -> service.registerUser(registrationRequest));

        verify(userRepository).save(argThat(user -> Objects.equals(user.getEmail(), registrationRequest.getEmail())));
    }
}
