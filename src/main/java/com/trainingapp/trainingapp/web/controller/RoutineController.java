package com.trainingapp.trainingapp.web.controller;

import com.trainingapp.trainingapp.application.usecase.CreateRoutineUseCase;
import com.trainingapp.trainingapp.web.dto.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    private final CreateRoutineUseCase createRoutineUseCase;

    public RoutineController(CreateRoutineUseCase createRoutineUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateRoutineResponse> createRoutine(@RequestBody CreateRoutineRequest routineRequest) {
        CreateRoutineResponse response = createRoutineUseCase.execute(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}