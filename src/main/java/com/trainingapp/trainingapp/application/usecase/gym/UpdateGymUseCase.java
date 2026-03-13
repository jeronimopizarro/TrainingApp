package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateGymUseCase {
    private final GymRepository gymRepository;
    private final SecurityUtils securityUtils;

    public UpdateGymUseCase(GymRepository gymRepository, SecurityUtils securityUtils) {
        this.gymRepository = gymRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public GymResponse execute(Long id, UpdateGymRequest request) {
        Gym gym = findGymOrThrow(id);

        securityUtils.validateSameGym(gym.getId());

        gym.updateDetails(request.name(), request.address(), request.phone());

        Gym updatedGym = gymRepository.save(gym);

        return mapToResponse(updatedGym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));
    }

    private GymResponse mapToResponse(Gym updatedGym) {
        return new GymResponse(updatedGym.getId(), updatedGym.getName(), updatedGym.getAddress(),
                updatedGym.getPhone(), updatedGym.isActive());
    }
}