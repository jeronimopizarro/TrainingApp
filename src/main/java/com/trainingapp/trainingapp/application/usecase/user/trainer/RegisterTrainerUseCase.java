package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
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

    public RegisterTrainerUseCase(TrainerRepository trainerRepository, GymRepository gymRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder, SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public TrainerResponse execute(RegisterTrainerRequest request) {
        securityUtils.validateSameGym(request.gymId());

        validateGymExists(request.gymId());
        validateEmailIsUnique(request.email());

        Trainer trainer = buildTrainerFromRequest(request);
        Trainer savedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(savedTrainer);
    }

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

    private Trainer buildTrainerFromRequest(RegisterTrainerRequest request) {
        return new Trainer(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.gymId(),
                request.specialization()
        );
    }

    private TrainerResponse buildResponseFromTrainer(Trainer trainer) {
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail(),
                trainer.getGymId(),
                trainer.getSpecialization(),
                trainer.isActive()
        );
    }
}