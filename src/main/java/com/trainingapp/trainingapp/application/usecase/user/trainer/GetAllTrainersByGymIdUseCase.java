package com.trainingapp.trainingapp.application.usecase.user.trainer;

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

    public GetAllTrainersByGymIdUseCase(TrainerRepository trainerRepository,
                                        SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    public List<TrainerResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<Trainer> trainers = trainerRepository.findByGymId(gymId);

        return mapToResponseList(trainers);
    }

    private List<TrainerResponse> mapToResponseList(List<Trainer> trainers) {
        return trainers.stream()
                .map(this::buildResponseFromTrainer)
                .toList();
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