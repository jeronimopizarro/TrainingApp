package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetAllGymsUseCase {

    private final GymRepository gymRepository;
    private final GymDTOMapper gymDTOMapper;

    public GetAllGymsUseCase(GymRepository gymRepository, GymDTOMapper gymDTOMapper) {
        this.gymRepository = gymRepository;
        this.gymDTOMapper = gymDTOMapper;
    }

    public List<GymResponse> execute() {
        List<Gym> gyms = gymRepository.findAll();

        return gyms.stream()
                .map(gymDTOMapper::toResponse)
                .toList();
    }
}