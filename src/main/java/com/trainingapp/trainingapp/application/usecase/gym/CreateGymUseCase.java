package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateGymUseCase {

    private final GymRepository gymRepository;

    public CreateGymUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Transactional
    public GymResponse execute(CreateGymRequest request) {
        Gym gym = buildGymEntity(request);

        Gym savedGym = gymRepository.save(gym);

        return mapToResponse(savedGym);
    }

    private Gym buildGymEntity(CreateGymRequest request) {
        return new Gym(request.name(), request.address(), request.phone());
    }

    private GymResponse mapToResponse(Gym savedGym) {
        return new GymResponse(
                savedGym.getId(), savedGym.getName(),
                savedGym.getAddress(), savedGym.getPhone(), savedGym.isActive());
    }
}