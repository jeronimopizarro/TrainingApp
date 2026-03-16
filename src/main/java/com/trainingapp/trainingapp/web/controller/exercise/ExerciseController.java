package com.trainingapp.trainingapp.web.controller.exercise;

import com.trainingapp.trainingapp.application.useCase.exercise.*;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import com.trainingapp.trainingapp.web.dto.exercise.UpdateExerciseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final CreateExerciseUseCase createExerciseUseCase;
    private final GetExerciseByIdUseCase getExerciseByIdUseCase;
    private final GetExercisesUseCase getExercisesUseCase;
    private final UpdateExerciseUseCase updateExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;

    public ExerciseController(CreateExerciseUseCase createExerciseUseCase,
                              GetExerciseByIdUseCase getByIdUseCase,
                              GetExercisesUseCase getExercisesUseCase,
                              UpdateExerciseUseCase updateExerciseUseCase,
                              DeleteExerciseUseCase deleteExerciseUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
        this.getExerciseByIdUseCase = getByIdUseCase;
        this.getExercisesUseCase = getExercisesUseCase;
        this.updateExerciseUseCase = updateExerciseUseCase;
        this.deleteExerciseUseCase = deleteExerciseUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody CreateExerciseRequest request) {
        ExerciseResponse response = createExerciseUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getExerciseByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<ExerciseDetailResponse>> getAllByMuscleGroup(
            @RequestParam(required = false) Long muscleGroupId) {
        return ResponseEntity.ok(getExercisesUseCase.execute(muscleGroupId));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExerciseRequest request) {
        ExerciseResponse response = updateExerciseUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteExerciseUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}