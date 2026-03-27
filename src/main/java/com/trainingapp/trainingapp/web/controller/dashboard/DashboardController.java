package com.trainingapp.trainingapp.web.controller.dashboard;

import com.trainingapp.trainingapp.application.useCase.dashboard.AdminDashboardUseCase;
import com.trainingapp.trainingapp.application.useCase.dashboard.MemberDashboardUseCase;
import com.trainingapp.trainingapp.application.useCase.dashboard.TrainerDashboardUseCase;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.TrainerDashboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final AdminDashboardUseCase adminDashboardUseCase;
    private final MemberDashboardUseCase memberDashboardUseCase;
    private final TrainerDashboardUseCase trainerDashboardUseCase;

    public DashboardController(AdminDashboardUseCase adminDashboardUseCase,
                               MemberDashboardUseCase memberDashboardUseCase,
                               TrainerDashboardUseCase trainerDashboardUseCase) {
        this.adminDashboardUseCase = adminDashboardUseCase;
        this.memberDashboardUseCase = memberDashboardUseCase;
        this.trainerDashboardUseCase = trainerDashboardUseCase;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('GYM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        AdminDashboardResponse response = adminDashboardUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberDashboardResponse> getMemberDashboard() {
        MemberDashboardResponse response = memberDashboardUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerDashboardResponse> getTrainerDashboard() {
        TrainerDashboardResponse response = trainerDashboardUseCase.execute();
        return ResponseEntity.ok(response);
    }
}