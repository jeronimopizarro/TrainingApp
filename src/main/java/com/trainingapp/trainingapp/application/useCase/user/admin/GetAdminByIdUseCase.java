package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import org.springframework.stereotype.Service;

@Service
public class GetAdminByIdUseCase {

    private final AdminRepository adminRepository;
    private final SecurityUtils securityUtils;
    private final AdminDTOMapper adminDTOMapper;
    private final UserAccessValidator userAccessValidator;

    public GetAdminByIdUseCase(AdminRepository adminRepository, SecurityUtils securityUtils,
                               AdminDTOMapper adminDTOMapper,
                               UserAccessValidator userAccessValidator) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
        this.adminDTOMapper = adminDTOMapper;
        this.userAccessValidator = userAccessValidator;
    }

    public AdminResponse execute(Long id) {
        Admin admin = findAdminOrThrow(id);

        securityUtils.validateSameGym(admin.getGymId());
        userAccessValidator.validateReadPermission(admin);

        return adminDTOMapper.toResponse(admin);
    }

    private Admin findAdminOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));
    }
}