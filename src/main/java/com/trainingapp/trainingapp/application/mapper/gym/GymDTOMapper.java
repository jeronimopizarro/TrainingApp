package com.trainingapp.trainingapp.application.mapper.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import org.springframework.stereotype.Component;

@Component
public class GymDTOMapper {

    public Gym toDomain(CreateGymRequest request) {
        if (request == null) return null;

        return Gym.createNew(
                request.name(),
                request.address(),
                request.phoneNumber());
    }

    public GymResponse toResponse(Gym gym) {
        if (gym == null) return null;

        return new GymResponse(
                gym.getId(),
                gym.getName(),
                gym.getAddress(),
                gym.getPhoneNumber(),
                gym.isActive()
        );
    }
}