package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final TrainerDTOMapper trainerDTOMapper;
    private final UserRegistrationValidator registrationValidator;

    public RegisterTrainerUseCase(TrainerRepository trainerRepository,
                                  PasswordEncoder passwordEncoder, SecurityUtils securityUtils,
                                  TrainerDTOMapper trainerDTOMapper,
                                  UserRegistrationValidator registrationValidator) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.trainerDTOMapper = trainerDTOMapper;
        this.registrationValidator = registrationValidator;
    }

    @Transactional
    public TrainerResponse execute(RegisterTrainerRequest request) {
        securityUtils.validateSameGym(request.gymId());
        registrationValidator.validateEmailIsUnique(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        Trainer trainer = trainerDTOMapper.toDomain(request, encodedPassword);

        Trainer savedTrainer = trainerRepository.save(trainer);
        return trainerDTOMapper.toResponse(savedTrainer);
    }
}