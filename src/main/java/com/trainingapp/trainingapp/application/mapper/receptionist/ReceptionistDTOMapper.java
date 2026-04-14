package com.trainingapp.trainingapp.application.mapper.receptionist;

import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import org.springframework.stereotype.Component;

@Component
public class ReceptionistDTOMapper {
    public ReceptionistResponse toResponse(Receptionist receptionist) {
        if (receptionist == null) return null;
        return new ReceptionistResponse(
                receptionist.getId(),
                receptionist.getFirstName(),
                receptionist.getLastName(),
                receptionist.getEmail(),
                receptionist.getDni(),
                receptionist.getGymId(),
                receptionist.getRole(),
                receptionist.isActive()
        );
    }
}