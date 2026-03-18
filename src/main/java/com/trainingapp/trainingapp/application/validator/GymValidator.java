package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import org.springframework.stereotype.Component;

@Component
public class GymValidator {

    private final GymRepository gymRepository;

    public GymValidator(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public void validateExists(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new GymNotFoundException("El gimnasio con ID " + gymId + " no existe.");
        }
    }
}