package com.trainingapp.trainingapp.web.controller.routine;

import com.trainingapp.trainingapp.application.useCase.routine.*;
import com.trainingapp.trainingapp.web.dto.routine.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    private final CreatePersonalRoutineUseCase createPersonalRoutineUseCase;
    private final AssignRoutineUseCase assignRoutineUseCase;
    private final GetRoutineByIdUseCase getRoutineByIdUseCase;
    private final GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase;
    private final ActivateRoutineUseCase activateRoutineUseCase;
    private final GetActiveRoutineUseCase getActiveRoutineUseCase;
    private final InactiveRoutineUseCase inactiveRoutineUseCase;
    private final DuplicateRoutineUseCase duplicateRoutineUseCase;
    private final UpdateRoutineUseCase updateRoutineUseCase;
    private final DeleteRoutineUseCase deleteRoutineUseCase;
    private final CompleteRoutineUseCase completeRoutineUseCase;
    private final GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase;
    private final RequestRoutineUseCase requestRoutineUseCase;
    private final TakeRoutineRequestUseCase takeRoutineRequestUseCase;

    public RoutineController(
            CreatePersonalRoutineUseCase createPersonalRoutineUseCase,
            AssignRoutineUseCase assignRoutineUseCase, GetRoutineByIdUseCase getRoutineByIdUseCase,
            GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase,
            ActivateRoutineUseCase activateRoutineUseCase,
            GetActiveRoutineUseCase getActiveRoutineUseCase,
            InactiveRoutineUseCase inactiveRoutineUseCase,
            DuplicateRoutineUseCase duplicateRoutineUseCase,
            UpdateRoutineUseCase updateRoutineUseCase,
            DeleteRoutineUseCase deleteRoutineUseCase,
            CompleteRoutineUseCase completeRoutineUseCase,
            GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase,
            RequestRoutineUseCase requestRoutineUseCase,
            TakeRoutineRequestUseCase takeRoutineRequestUseCase) {
        this.createPersonalRoutineUseCase = createPersonalRoutineUseCase;
        this.assignRoutineUseCase = assignRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
        this.getAllRoutinesByMemberIdUseCase = getAllRoutinesByMemberIdUseCase;
        this.activateRoutineUseCase = activateRoutineUseCase;
        this.getActiveRoutineUseCase = getActiveRoutineUseCase;
        this.inactiveRoutineUseCase = inactiveRoutineUseCase;
        this.duplicateRoutineUseCase = duplicateRoutineUseCase;
        this.updateRoutineUseCase = updateRoutineUseCase;
        this.deleteRoutineUseCase = deleteRoutineUseCase;
        this.completeRoutineUseCase = completeRoutineUseCase;
        this.getAllRoutinesByTrainerIdUseCase = getAllRoutinesByTrainerIdUseCase;
        this.requestRoutineUseCase = requestRoutineUseCase;
        this.takeRoutineRequestUseCase = takeRoutineRequestUseCase;
    }

    @PostMapping("/personal")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<CreateRoutineResponse> createPersonalRoutine(@Valid @RequestBody CreatePersonalRoutineRequest request) {
        CreateRoutineResponse response = createPersonalRoutineUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<CreateRoutineResponse> assignRoutine(@Valid @RequestBody AssignRoutineRequest request) {
        CreateRoutineResponse response = assignRoutineUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PostMapping("/{routineId}/duplicate")
    public ResponseEntity<CreateRoutineResponse> duplicateRoutine(@PathVariable Long routineId,
                                                                  @Valid @RequestBody DuplicateRoutineRequest duplicateRoutineRequest) {
        CreateRoutineResponse response = duplicateRoutineUseCase.execute(routineId,
                duplicateRoutineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<RoutineDetailResponse> getRoutine(@PathVariable Long id) {
        RoutineDetailResponse response = getRoutineByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping()
    public ResponseEntity<List<GetAllRoutinesByMemberIdResponse>> getAllRoutinesByMember(
            @RequestParam Long memberId) {
        List<GetAllRoutinesByMemberIdResponse> response = getAllRoutinesByMemberIdUseCase.execute(
                memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @GetMapping(params = "trainerId")
    public ResponseEntity<List<GetAllRoutinesByTrainerIdResponse>> getAllRoutinesByTrainer(
            @RequestParam Long trainerId) {

        List<GetAllRoutinesByTrainerIdResponse> response =
                getAllRoutinesByTrainerIdUseCase.execute(trainerId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/active")
    public ResponseEntity<RoutineResponse> getActiveRoutine(@RequestParam Long memberId) {
        RoutineResponse response = getActiveRoutineUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<RoutineResponse> activateRoutine(@PathVariable Long id,
                                                @Valid @RequestBody
                                                ActivateRoutineRequest request) {
        RoutineResponse response = activateRoutineUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/inactive")
    public ResponseEntity<Void> inactiveRoutine(@PathVariable Long id) {
        inactiveRoutineUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeRoutine(@PathVariable Long id) {
        completeRoutineUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateRoutineResponse> updateRoutine(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateRoutineRequest request) {
        CreateRoutineResponse response = updateRoutineUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        deleteRoutineUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Void> requestRoutine(@Valid @RequestBody RequestRoutineMessage request) {
        requestRoutineUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/requests/{requestId}/take")
    @PreAuthorize("hasAnyRole('TRAINER', 'GYM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> takeRoutineRequest(@PathVariable Long requestId) {
        takeRoutineRequestUseCase.execute(requestId);
        return ResponseEntity.ok().build();
    }
}