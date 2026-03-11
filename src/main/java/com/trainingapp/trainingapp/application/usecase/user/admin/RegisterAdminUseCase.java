package com.trainingapp.trainingapp.application.usecase.user.admin;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterAdminUseCase(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminResponse execute(RegisterAdminRequest request) {
        Admin admin = buildAdminFromRequest(request);

        Admin savedAdmin = adminRepository.save(admin);

        return buildResponseFromAdmin(savedAdmin);
    }

    private Admin buildAdminFromRequest(RegisterAdminRequest request) {
        return new Admin(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role(),
                request.gymId()
        );
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