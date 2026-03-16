package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminMapper {

    public AdminJpaEntity toJpaEntity(Admin domain) {
        if (domain == null) return null;

        AdminJpaEntity entity = new AdminJpaEntity();

        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());

        if (!domain.isActive()) entity.setDeletedAt(LocalDateTime.now());

        return entity;
    }

    public Admin toDomain(AdminJpaEntity entity) {
        if (entity == null) return null;

        Admin admin = new Admin(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getGymId()
        );

        admin.setId(entity.getId());
        admin.setActive(entity.isActive());

        return admin;
    }

    public Admin toDomain(RegisterAdminRequest request, String encodedPassword) {
        if (request == null) return null;
        return new Admin(
                request.firstName(),
                request.lastName(),
                request.email(),
                encodedPassword,
                request.role(),
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
                admin.getRole(),
                admin.getGymId(),
                admin.isActive()
        );
    }
}