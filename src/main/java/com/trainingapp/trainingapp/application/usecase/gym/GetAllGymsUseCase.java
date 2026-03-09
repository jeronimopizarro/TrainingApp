package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetAllGymsUseCase {

    private final GymRepository gymRepository;

    public GetAllGymsUseCase(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public List<GymResponse> execute() {
        List<Gym> gyms = gymRepository.findAll();

        return gyms.stream()
                .map(gym -> new GymResponse(gym.getId(), gym.getName(), gym.getAddress(),
                        gym.getPhone(), gym.isActive())).toList();
    }
}
