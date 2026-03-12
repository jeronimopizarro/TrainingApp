package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class GetGymByIdUseCase {

    private final GymRepository gymRepository;

    public GetGymByIdUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Transactional
    public GymResponse execute(Long id) {
        Gym gym = findGymOrThrow(id);

        return mapToResponse(gym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new GymNotFoundException(
                        "The gym with id " + id + " was not found."));
    }

    private GymResponse mapToResponse(Gym gym) {
        return new GymResponse(gym.getId(), gym.getName(), gym.getAddress(), gym.getPhone(),
                gym.isActive());
    }
}