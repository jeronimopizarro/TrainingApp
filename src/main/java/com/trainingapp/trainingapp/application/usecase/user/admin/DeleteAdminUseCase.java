package com.trainingapp.trainingapp.application.usecase.user.admin;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteAdminUseCase {

    private final AdminRepository adminRepository;

    public DeleteAdminUseCase(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public void execute(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException("Admin with id " + id + " was not found."));

        admin.deactivate();

        adminRepository.save(admin);
    }
}