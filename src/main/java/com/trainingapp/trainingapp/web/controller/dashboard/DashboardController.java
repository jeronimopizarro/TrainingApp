package com.trainingapp.trainingapp.web.controller.dashboard;

import com.trainingapp.trainingapp.application.useCase.dashboard.AdminDashboardUseCase;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final AdminDashboardUseCase adminDashboardUseCase;

    public DashboardController(AdminDashboardUseCase adminDashboardUseCase) {
        this.adminDashboardUseCase = adminDashboardUseCase;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('GYM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        AdminDashboardResponse response = adminDashboardUseCase.execute();
        return ResponseEntity.ok(response);
    }
}