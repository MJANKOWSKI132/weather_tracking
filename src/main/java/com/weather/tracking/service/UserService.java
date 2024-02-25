package com.weather.tracking.service;

import com.weather.tracking.audit.Auditable;
import com.weather.tracking.dto.request.UserRegistrationRequestDto;
import com.weather.tracking.dto.response.UserRegistrationResponseDto;
import com.weather.tracking.entity.User;
import com.weather.tracking.enums.AuditAction;
import com.weather.tracking.exception.UserAlreadyExistsException;
import com.weather.tracking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Auditable(action = AuditAction.REGISTER_USER)
    public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userRegistrationRequest) throws UserAlreadyExistsException {
        boolean userAlreadyExists = userRepository.existsByEmail(userRegistrationRequest.getEmail());
        if (userAlreadyExists)
            throw new UserAlreadyExistsException(userRegistrationRequest.getEmail());
        User user = new User(userRegistrationRequest.getName(), userRegistrationRequest.getEmail());
        userRepository.save(user);
        return new UserRegistrationResponseDto(user.getId());
    }
}
