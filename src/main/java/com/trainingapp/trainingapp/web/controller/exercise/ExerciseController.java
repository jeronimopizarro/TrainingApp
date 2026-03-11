package com.trainingapp.trainingapp.web.controller.exercise;

import com.trainingapp.trainingapp.application.usecase.exercise.*;
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
    private final GetExerciseByIdUseCase getByIdUseCase;
    private final GetAllExercisesUseCase getAllUseCase;
    private final UpdateExerciseUseCase updateExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;

    public ExerciseController(CreateExerciseUseCase createExerciseUseCase,
                              GetExerciseByIdUseCase getByIdUseCase,
                              GetAllExercisesUseCase getAllUseCase,
                              UpdateExerciseUseCase updateExerciseUseCase,
                              DeleteExerciseUseCase deleteExerciseUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
        this.getByIdUseCase = getByIdUseCase;
        this.getAllUseCase = getAllUseCase;
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
        return ResponseEntity.ok(getByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<ExerciseDetailResponse>> getAll(
            @RequestParam(required = false) Long muscleGroupId) {
        return ResponseEntity.ok(getAllUseCase.execute(muscleGroupId));
    }

    //TODO: Logica de negocio para que un trainer solo pueda modificar el ejercicio que el mismo creo.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExerciseRequest request) {
        ExerciseResponse response = updateExerciseUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteExerciseUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}