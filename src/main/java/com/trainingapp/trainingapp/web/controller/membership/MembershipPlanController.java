package com.trainingapp.trainingapp.web.controller.membership;

import com.trainingapp.trainingapp.application.useCase.membership.CreateMembershipPlanUseCase;
import com.trainingapp.trainingapp.application.useCase.membership.DeleteMembershipPlanUseCase;
import com.trainingapp.trainingapp.application.useCase.membership.GetAllMembershipPlansByGymIdUseCase;
import com.trainingapp.trainingapp.application.useCase.membership.UpdateMembershipPlanUseCase;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membership-plans")
public class MembershipPlanController {

    private final CreateMembershipPlanUseCase createUseCase;
    private final UpdateMembershipPlanUseCase updateUseCase;
    private final DeleteMembershipPlanUseCase deleteUseCase;
    private final GetAllMembershipPlansByGymIdUseCase getAllUseCase;

    public MembershipPlanController(CreateMembershipPlanUseCase createUseCase,
                                    UpdateMembershipPlanUseCase updateUseCase,
                                    DeleteMembershipPlanUseCase deleteUseCase,
                                    GetAllMembershipPlansByGymIdUseCase getAllUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getAllUseCase = getAllUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PostMapping
    public ResponseEntity<MembershipPlanResponse> create(@RequestBody CreateMembershipPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MembershipPlanResponse> update(@PathVariable Long id, @RequestBody UpdateMembershipPlanRequest request) {
        return ResponseEntity.ok(updateUseCase.execute(id, request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER', 'RECEPTIONIST')")
    @GetMapping
    public ResponseEntity<List<MembershipPlanResponse>> getAllByGymId(@RequestParam Long gymId) {
        return ResponseEntity.ok(getAllUseCase.execute(gymId));
    }
}