package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
import org.springframework.stereotype.Service;

@Service
public class UpdateGymUseCase {
    private final GymRepository gymRepository;

    public UpdateGymUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public GymResponse execute(Long id, UpdateGymRequest request) {
        Gym gym = gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));

        gym.updateDetails(request.name(), request.address(), request.phone());

        Gym updatedGym = gymRepository.save(gym);

        return new GymResponse(updatedGym.getId(), updatedGym.getName(), updatedGym.getAddress(),
                updatedGym.getPhone(), updatedGym.isActive());
    }
}