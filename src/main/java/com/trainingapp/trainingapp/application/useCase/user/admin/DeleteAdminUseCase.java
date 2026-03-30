package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteAdminUseCase {

    private final AdminRepository adminRepository;
    private final SecurityUtils securityUtils;
    private final UserAccessValidator userAccessValidator;

    public DeleteAdminUseCase(AdminRepository adminRepository, SecurityUtils securityUtils,
                              UserAccessValidator userAccessValidator) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Admin admin = findAdminOrThrow(id);

        securityUtils.validateSameGym(admin.getGymId());
        userAccessValidator.validateWritePermission(admin.getId());

        admin.deactivate();
        adminRepository.save(admin);
    }

    private Admin findAdminOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));
    }
}