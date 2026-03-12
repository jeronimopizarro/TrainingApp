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
    private final GetExerciseByIdUseCase getExerciseByIdUseCase;
    private final GetAllExercisesByGroupIdUseCase getAllExercisesByGroupIdUseCase;
    private final UpdateExerciseUseCase updateExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;

    public ExerciseController(CreateExerciseUseCase createExerciseUseCase,
                              GetExerciseByIdUseCase getByIdUseCase,
                              GetAllExercisesByGroupIdUseCase getAllExercisesByGroupIdUseCase,
                              UpdateExerciseUseCase updateExerciseUseCase,
                              DeleteExerciseUseCase deleteExerciseUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
        this.getExerciseByIdUseCase = getByIdUseCase;
        this.getAllExercisesByGroupIdUseCase = getAllExercisesByGroupIdUseCase;
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

    //TODO: logica de negocio para que el member pueda solicitar todos los ejercicio de su gimnasio donde esta registrado.
    //TODO: logica de negocio para que el trainer pueda solicitar todos los ejercicio de su gimnasio donde trabaja.
    //TODO: logica de negocio para que el gym admin pueda solicitar todos los ejercicio de su propio gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<ExerciseDetailResponse>> getAllByMuscleGroup(
            @RequestParam(required = false) Long muscleGroupId) {
        return ResponseEntity.ok(getAllExercisesByGroupIdUseCase.execute(muscleGroupId));
    }

    //TODO: logica de negocio para que el trainer pueda modificar si el ejercicio es solo del gimnasio donde trabaja.
    //TODO: logica de negocio para que el gym admin pueda modificar si el ejercicio es de su propio gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExerciseRequest request) {
        ExerciseResponse response = updateExerciseUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio para que el gym admin pueda eliminar el ejercicio de su propio gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteExerciseUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}