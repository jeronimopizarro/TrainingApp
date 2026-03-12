package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
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
        User currentUser = securityUtils.getCurrentUser();

        validateRegistrationAccess(currentUser, request.gymId());

        validateGymExists(request.gymId());

        validateEmailIsUnique(request.email());

        Trainer trainer = buildTrainerFromRequest(request);

        Trainer savedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(savedTrainer);
    }

    private void validateRegistrationAccess(User currentUser, Long targetGymId) {
        if (currentUser.getRole() == Role.GYM_ADMIN) {
            Admin admin = (Admin) currentUser;
            if (!admin.getGymId().equals(targetGymId)) {
                throw new AccessDeniedException(
                        "Solo puedes registrar entrenadores para tu propio gimnasio.");
            }
        }
    }

    private void validateGymExists(Long gymId) {
        gymRepository.findById(gymId)
                .orElseThrow(() -> new GymNotFoundException(
                        "No se puede registrar al usuario. El gimnasio con ID " + gymId + " no existe."));
    }

    private void validateEmailIsUnique(String email) {
        // Usamos el findByEmail de tu UserRepository y validamos si está presente
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