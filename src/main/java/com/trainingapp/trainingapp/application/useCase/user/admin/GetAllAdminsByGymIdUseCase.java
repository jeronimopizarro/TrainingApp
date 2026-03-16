package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllAdminsByGymIdUseCase {

    private final AdminRepository adminRepository;
    private final SecurityUtils securityUtils;
    private final AdminDTOMapper adminDTOMapper;

    public GetAllAdminsByGymIdUseCase(AdminRepository adminRepository, SecurityUtils securityUtils,
                                      AdminDTOMapper adminDTOMapper) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
        this.adminDTOMapper = adminDTOMapper;
    }

    public List<AdminResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<Admin> admins = adminRepository.findByGymId(gymId);

        return admins.stream().map(adminDTOMapper::toResponse).toList();
    }
}