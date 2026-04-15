package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.useCase.user.staff.GetStaffSummaryUseCase;
import com.trainingapp.trainingapp.web.dto.user.staff.StaffSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final GetStaffSummaryUseCase getStaffSummaryUseCase;

    public StaffController(GetStaffSummaryUseCase getStaffSummaryUseCase) {
        this.getStaffSummaryUseCase = getStaffSummaryUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'MEMBER')")
    @GetMapping("/summary")
    public ResponseEntity<StaffSummaryResponse> getSummary(
            @RequestParam Long gymId,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(getStaffSummaryUseCase.execute(gymId, role));
    }
}
