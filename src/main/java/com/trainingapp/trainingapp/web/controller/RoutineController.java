package com.trainingapp.trainingapp.web.controller;

import com.trainingapp.trainingapp.application.usecase.CreateRoutineUseCase;
import com.trainingapp.trainingapp.application.usecase.GetRoutineByIdUseCase;
import com.trainingapp.trainingapp.web.dto.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.GetRoutineByIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    private final CreateRoutineUseCase createRoutineUseCase;
    private final GetRoutineByIdUseCase getRoutineByIdUseCase;

    public RoutineController(CreateRoutineUseCase createRoutineUseCase,
                             GetRoutineByIdUseCase getRoutineByIdUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateRoutineResponse> createRoutine(@RequestBody CreateRoutineRequest routineRequest) {
        CreateRoutineResponse response = createRoutineUseCase.execute(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetRoutineByIdResponse> getRoutine(@PathVariable Long id) {
        GetRoutineByIdResponse response = getRoutineByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}