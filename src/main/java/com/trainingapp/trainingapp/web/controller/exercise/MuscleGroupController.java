package com.trainingapp.trainingapp.web.controller.exercise;

import com.trainingapp.trainingapp.application.usecase.exercise.muscleGroup.GetAllMuscleGroupsUseCase;
import com.trainingapp.trainingapp.application.usecase.exercise.muscleGroup.GetMuscleGroupByIdUseCase;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/muscle-groups")
public class MuscleGroupController {

    private final GetAllMuscleGroupsUseCase getAllUseCase;
    private final GetMuscleGroupByIdUseCase getByIdUseCase;

    public MuscleGroupController(GetAllMuscleGroupsUseCase getAllUseCase, GetMuscleGroupByIdUseCase getByIdUseCase) {
        this.getAllUseCase = getAllUseCase;
        this.getByIdUseCase = getByIdUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<MuscleGroupResponse>> getAll() {
        return ResponseEntity.ok(getAllUseCase.execute());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MuscleGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getByIdUseCase.execute(id));
    }
}