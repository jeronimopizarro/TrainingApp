package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GetGymByIdUseCase {

    private final GymRepository gymRepository;
    private final SecurityUtils securityUtils;
    private final GymDTOMapper gymDTOMapper;

    public GetGymByIdUseCase(GymRepository gymRepository, SecurityUtils securityUtils,
                             GymDTOMapper gymDTOMapper) {
        this.gymRepository = gymRepository;
        this.securityUtils = securityUtils;
        this.gymDTOMapper = gymDTOMapper;
    }

    @Transactional
    public GymResponse execute(Long id) {
        Gym gym = findGymOrThrow(id);

        securityUtils.validateSameGym(gym.getId());

        return gymDTOMapper.toResponse(gym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new GymNotFoundException(
                        "The gym with id " + id + " was not found."));
    }
}