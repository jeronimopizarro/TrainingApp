package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
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
    private final AdminDTOMapper adminDTOMapper;
    private final UserAccessValidator userAccessValidator;

    public UpdateAdminUseCase(AdminRepository adminRepository, SecurityUtils securityUtils,
                              AdminDTOMapper adminDTOMapper,
                              UserAccessValidator userAccessValidator) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
        this.adminDTOMapper = adminDTOMapper;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public AdminResponse execute(Long id, UpdateAdminRequest request) {
        Admin admin = findAdminOrThrow(id);

        securityUtils.validateSameGym(admin.getGymId());
        userAccessValidator.validateWritePermission(admin.getId());

        admin.updateProfile(request.firstName(), request.lastName());
        Admin updatedAdmin = adminRepository.save(admin);

        return adminDTOMapper.toResponse(updatedAdmin);
    }

    private Admin findAdminOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));
    }
}