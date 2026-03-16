package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationValidator {

    private final UserRepository userRepository;

    public UserRegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailIsUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email " + email + " ya está registrado en el sistema.");
        }
    }
}
