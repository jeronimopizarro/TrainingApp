package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.DuplicateGymNameException;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym.GymMapper;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateGymUseCase {
    private final GymRepository gymRepository;
    private final SecurityUtils securityUtils;
    private final GymMapper gymMapper;

    public UpdateGymUseCase(GymRepository gymRepository, SecurityUtils securityUtils,
                            GymMapper gymMapper) {
        this.gymRepository = gymRepository;
        this.securityUtils = securityUtils;
        this.gymMapper = gymMapper;
    }

    @Transactional
    public GymResponse execute(Long id, UpdateGymRequest request) {
        Gym gym = findGymOrThrow(id);

        securityUtils.validateSameGym(gym.getId());
        validateGymNameIsUniqueForUpdate(request.name(), id);

        gym.updateDetails(request.name(), request.address(), request.phone());

        Gym updatedGym = gymRepository.save(gym);
        return gymMapper.toResponse(updatedGym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));
    }

    private void validateGymNameIsUniqueForUpdate(String name, Long currentId) {
        if (gymRepository.existsByNameAndIdNot(name, currentId)){
            throw new DuplicateGymNameException("The gym with name " + name + " already exists.");
        }
    }
}