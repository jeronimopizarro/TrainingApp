package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteGymUseCase {
    private final GymRepository gymRepository;

    public DeleteGymUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public void execute(Long id) {
        Gym gym = gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));

        gym.desactive();

        gymRepository.save(gym);
    }
}
