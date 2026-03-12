package com.trainingapp.trainingapp.application.usecase.user.admin;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.UpdateAdminRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateAdminUseCase {

    private final AdminRepository adminRepository;
    private final SecurityUtils securityUtils;

    public UpdateAdminUseCase(AdminRepository adminRepository, SecurityUtils securityUtils) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public AdminResponse execute(Long id, UpdateAdminRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        validateOwnership(currentUser, id);

        Admin admin = findAdminOrThrow(id);

        updateAdminFields(admin, request);

        Admin updatedAdmin = adminRepository.save(admin);

        return buildResponseFromAdmin(updatedAdmin);
    }

    private void validateOwnership(User currentUser, Long targetId) {
        if (currentUser.getRole() == Role.GYM_ADMIN && !currentUser.getId().equals(targetId)) {
            throw new AccessDeniedException("Solo puedes modificar tu propio perfil.");
        }
    }

    private Admin findAdminOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException(
                        "Admin with id " + id + " was not found."));
    }

    private void updateAdminFields(Admin admin, UpdateAdminRequest request) {
        if (request.firstName() != null && !request.firstName().isBlank()) {
            admin.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            admin.setLastName(request.lastName());
        }
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