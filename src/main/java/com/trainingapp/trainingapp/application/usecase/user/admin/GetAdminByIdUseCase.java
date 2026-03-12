package com.trainingapp.trainingapp.application.usecase.user.admin;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetAdminByIdUseCase {

    private final AdminRepository adminRepository;
    private final SecurityUtils securityUtils;

    public GetAdminByIdUseCase(AdminRepository adminRepository, SecurityUtils securityUtils) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
    }

    public AdminResponse execute(Long id) {
        Admin admin = findAdminOrThrow(id);

        securityUtils.validateSameGym(admin.getGymId());

        return buildResponseFromAdmin(admin);
    }

    private Admin findAdminOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException(
                        "Admin with id " + id + " was not found."));
    }

    private AdminResponse buildResponseFromAdmin(Admin admin) {
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