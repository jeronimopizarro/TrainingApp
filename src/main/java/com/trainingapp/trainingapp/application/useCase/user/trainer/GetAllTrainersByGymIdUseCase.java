package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllTrainersByGymIdUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils  securityUtils;
    private final GymValidator gymValidator;
    private final TrainerDTOMapper trainerDTOMapper;

    public GetAllTrainersByGymIdUseCase(TrainerRepository trainerRepository,
                                        SecurityUtils securityUtils, GymValidator gymValidator,
                                        TrainerDTOMapper trainerDTOMapper) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
        this.gymValidator = gymValidator;
        this.trainerDTOMapper = trainerDTOMapper;
    }

    public List<TrainerResponse> execute(Long gymId) {
        gymValidator.validateExists(gymId);
        securityUtils.validateSameGym(gymId);

        List<Trainer> trainers = trainerRepository.findByGymId(gymId);

        return trainers.stream()
                .map(trainerDTOMapper::toResponse)
                .toList();
    }
}