package com.trainingapp.trainingapp.application.mapper.admin;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import org.springframework.stereotype.Component;

@Component
public class AdminDTOMapper {

    public Admin toDomain(RegisterAdminRequest request, Role role, String encodedPassword) {
        if (request == null) return null;
        return Admin.createNew(
                request.firstName(),
                request.lastName(),
                request.email(),
                encodedPassword,
                request.dni(),
                role,
                request.gymId()
        );
    }

    public AdminResponse toResponse(Admin admin) {
        if (admin == null) return null;
        return new AdminResponse(
                admin.getId(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getEmail(),
                admin.getDni(),
                admin.getRole(),
                admin.getGymId(),
                admin.isActive()
        );
    }
}