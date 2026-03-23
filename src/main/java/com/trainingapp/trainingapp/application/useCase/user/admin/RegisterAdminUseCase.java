package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminDTOMapper adminDTOMapper;
    private final UserRegistrationValidator registrationValidator;
    private final GymValidator gymValidator;
    private final SecurityUtils securityUtils;

    public RegisterAdminUseCase(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                                AdminDTOMapper adminDTOMapper,
                                UserRegistrationValidator registrationValidator,
                                GymValidator gymValidator, SecurityUtils securityUtils) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminDTOMapper = adminDTOMapper;
        this.registrationValidator = registrationValidator;
        this.gymValidator = gymValidator;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public AdminResponse execute(RegisterAdminRequest request) {
        if (request.role() == Role.GYM_ADMIN) {
            gymValidator.validateExists(request.gymId());
            securityUtils.validateSameGym(request.gymId());
        }
        registrationValidator.validateEmailIsUnique(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        Admin admin = adminDTOMapper.toDomain(request, encodedPassword);

        Admin savedAdmin = adminRepository.save(admin);

        return adminDTOMapper.toResponse(savedAdmin);
    }
}