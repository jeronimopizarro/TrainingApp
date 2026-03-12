package com.trainingapp.trainingapp.application.usecase.user.admin;

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

    public GetAllAdminsByGymIdUseCase(AdminRepository adminRepository, SecurityUtils securityUtils) {
        this.adminRepository = adminRepository;
        this.securityUtils = securityUtils;
    }

    public List<AdminResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<Admin> admins = adminRepository.findByGymId(gymId);

        return mapToResponseList(admins);
    }

    private List<AdminResponse> mapToResponseList(List<Admin> admins) {
        return admins.stream()
                .map(this::buildResponseFromAdmin)
                .toList();
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