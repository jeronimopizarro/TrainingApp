package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateGymUseCase {

    private final GymRepository gymRepository;

    public CreateGymUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public CreateGymResponse execute(CreateGymRequest request) {
        Gym gym = new Gym(request.name(), request.address(), request.phone());

        Gym savedGym = gymRepository.save(gym);

        return new CreateGymResponse(
                savedGym.getId(),savedGym.getName(),
                savedGym.getAddress(), savedGym.getPhone(), savedGym.isActive());
    }
}