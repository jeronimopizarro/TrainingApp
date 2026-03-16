package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.TrainerMapper;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final TrainerMapper trainerMapper;
    private final UserRegistrationValidator registrationValidator;

    public RegisterTrainerUseCase(TrainerRepository trainerRepository, GymRepository gymRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder, SecurityUtils securityUtils,
                                  TrainerMapper trainerMapper,
                                  UserRegistrationValidator registrationValidator) {
        this.trainerRepository = trainerRepository;
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.trainerMapper = trainerMapper;
        this.registrationValidator = registrationValidator;
    }

    @Transactional
    public TrainerResponse execute(RegisterTrainerRequest request) {
        securityUtils.validateSameGym(request.gymId());
        registrationValidator.validateEmailIsUnique(request.email());

        //TODO: validar la existencia del gym y que el email este registrado.
        //validateGymExists(request.gymId());
        //validateEmailIsUnique(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        Trainer trainer = trainerMapper.toDomain(request, encodedPassword);

        Trainer savedTrainer = trainerRepository.save(trainer);

        return trainerMapper.toResponse(savedTrainer);
    }

    /*
    private void validateGymExists(Long gymId) {
        gymRepository.findById(gymId)
                .orElseThrow(() -> new GymNotFoundException(
                        "No se puede registrar al usuario. El gimnasio con ID " + gymId + " no existe."));
    }

    private void validateEmailIsUnique(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "El email " + email + " ya se encuentra registrado en el sistema.");
        }
    }
    */
}