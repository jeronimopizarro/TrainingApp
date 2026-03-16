package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym.GymMapper;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateGymUseCase {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

    public CreateGymUseCase(GymRepository gymRepository, GymMapper gymMapper) {
        this.gymRepository = gymRepository;
        this.gymMapper = gymMapper;
    }

    @Transactional
    public GymResponse execute(CreateGymRequest request) {
        validateGymNameIsUnique(request.name());

        Gym gym = gymMapper.toDomain(request);

        Gym savedGym = gymRepository.save(gym);

        return gymMapper.toResponse(savedGym);
    }

    private void validateGymNameIsUnique(String name) {
        if (gymRepository.existsByName(name)) {
            throw new IllegalArgumentException("Ya existe un gimnasio activo con el nombre: " + name);
        }
    }
}