package com.trainingapp.trainingapp.web.controller.routine;

import com.trainingapp.trainingapp.application.usecase.routine.*;
import com.trainingapp.trainingapp.web.dto.routine.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    private final CreateRoutineUseCase createRoutineUseCase;
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

    public RoutineController(CreateRoutineUseCase createRoutineUseCase,
                             GetRoutineByIdUseCase getRoutineByIdUseCase,
                             GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase,
                             ActivateRoutineUseCase activateRoutineUseCase,
                             GetActiveRoutineUseCase getActiveRoutineUseCase,
                             InactiveRoutineUseCase inactiveRoutineUseCase,
                             DuplicateRoutineUseCase duplicateRoutineUseCase,
                             UpdateRoutineUseCase updateRoutineUseCase,
                             DeleteRoutineUseCase deleteRoutineUseCase,
                             CompleteRoutineUseCase completeRoutineUseCase,
                             GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
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
    }

    @PostMapping
    public ResponseEntity<CreateRoutineResponse> createRoutine(
            @Valid @RequestBody CreateRoutineRequest routineRequest) {
        CreateRoutineResponse response = createRoutineUseCase.execute(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{routineId}/duplicate")
    public ResponseEntity<CreateRoutineResponse> duplicateRoutine(@PathVariable Long routineId,
                                                                  @Valid @RequestBody DuplicateRoutineRequest duplicateRoutineRequest) {
        CreateRoutineResponse response = duplicateRoutineUseCase.execute(routineId,
                duplicateRoutineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineDetailResponse> getRoutine(@PathVariable Long id) {
        RoutineDetailResponse response = getRoutineByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<GetAllRoutinesByMemberIdResponse>> getAllRoutinesByMember(
            @RequestParam Long memberId) {
        List<GetAllRoutinesByMemberIdResponse> response = getAllRoutinesByMemberIdUseCase.execute(
                memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "trainerId")
    public ResponseEntity<List<GetAllRoutinesByTrainerIdResponse>> getAllRoutinesByTrainer(
            @RequestParam Long trainerId) {

        List<GetAllRoutinesByTrainerIdResponse> response = getAllRoutinesByTrainerIdUseCase.execute(trainerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<RoutineResponse> getActiveRoutine(@RequestParam Long memberId) {
        RoutineResponse response = getActiveRoutineUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateRoutine(@PathVariable Long id,
                                                @Valid @RequestBody
                                                ActivateRoutineRequest request) {
        activateRoutineUseCase.execute(id,
                request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<Void> inactiveRoutine(@PathVariable Long id,
                                                @RequestParam Long userId) {
        inactiveRoutineUseCase.execute(id,
                userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeRoutine(@PathVariable Long id,
                                                @RequestParam Long userId) {
        completeRoutineUseCase.execute(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateRoutineResponse> updateRoutine(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateRoutineRequest request) {
        CreateRoutineResponse response = updateRoutineUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id, @RequestParam Long userId) {
        deleteRoutineUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }
}