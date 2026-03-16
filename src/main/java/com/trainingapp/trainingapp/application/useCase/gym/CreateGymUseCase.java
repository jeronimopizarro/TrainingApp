package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateGymUseCase {

    private final GymRepository gymRepository;
    private final GymDTOMapper gymDTOMapper;

    public CreateGymUseCase(GymRepository gymRepository, GymDTOMapper gymDTOMapper) {
        this.gymRepository = gymRepository;
        this.gymDTOMapper = gymDTOMapper;
    }

    @Transactional
    public GymResponse execute(CreateGymRequest request) {
        validateGymNameIsUnique(request.name());

        Gym gym = gymDTOMapper.toDomain(request);

        Gym savedGym = gymRepository.save(gym);

        return gymDTOMapper.toResponse(savedGym);
    }

    private void validateGymNameIsUnique(String name) {
        if (gymRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Ya existe un gimnasio activo con el nombre: " + name);
        }
    }
}