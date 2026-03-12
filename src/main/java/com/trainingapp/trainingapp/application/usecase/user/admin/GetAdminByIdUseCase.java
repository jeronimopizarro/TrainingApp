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
    private final SecurityUtils  securityUtils;

    public GetAdminByIdUseCase(AdminRepository adminRepository, SecurityUtils securityUtils) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
    }

    public AdminResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        validateAccess(currentUser, id);

        Admin admin = findAdminOrThrow(id);

        return buildResponseFromAdmin(admin);
    }

    private void validateAccess(User currentUser, Long targetId) {
        if (currentUser.getRole() == Role.GYM_ADMIN && !currentUser.getId().equals(targetId)) {
            throw new AccessDeniedException("No tienes permiso para ver el perfil de otro administrador.");
        }
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