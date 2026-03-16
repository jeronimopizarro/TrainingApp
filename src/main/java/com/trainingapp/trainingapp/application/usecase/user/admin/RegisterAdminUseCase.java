package com.trainingapp.trainingapp.application.usecase.user.admin;

import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.AdminMapper;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;
    private final UserRegistrationValidator registrationValidator;

    public RegisterAdminUseCase(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                                AdminMapper adminMapper,
                                UserRegistrationValidator registrationValidator) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminMapper = adminMapper;
        this.registrationValidator = registrationValidator;
    }

    @Transactional
    public AdminResponse execute(RegisterAdminRequest request) {
        registrationValidator.validateEmailIsUnique(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        Admin admin = adminMapper.toDomain(request, encodedPassword);

        Admin savedAdmin = adminRepository.save(admin);

        return adminMapper.toResponse(savedAdmin);
    }
}