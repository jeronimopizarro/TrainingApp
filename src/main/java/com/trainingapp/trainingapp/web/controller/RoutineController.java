package com.trainingapp.trainingapp.web.controller;

import com.trainingapp.trainingapp.application.usecase.CreateRoutineUseCase;
import com.trainingapp.trainingapp.application.usecase.GetAllRoutinesByMemberIdUseCase;
import com.trainingapp.trainingapp.application.usecase.GetRoutineByIdUseCase;
import com.trainingapp.trainingapp.web.dto.CreateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.GetAllRoutinesByMemberIdResponse;
import com.trainingapp.trainingapp.web.dto.GetRoutineByIdResponse;
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

    public RoutineController(CreateRoutineUseCase createRoutineUseCase,
                             GetRoutineByIdUseCase getRoutineByIdUseCase,
                             GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
        this.getAllRoutinesByMemberIdUseCase = getAllRoutinesByMemberIdUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateRoutineResponse> createRoutine(@Valid @RequestBody CreateRoutineRequest routineRequest) {
        CreateRoutineResponse response = createRoutineUseCase.execute(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetRoutineByIdResponse> getRoutine(@PathVariable Long id) {
        GetRoutineByIdResponse response = getRoutineByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<GetAllRoutinesByMemberIdResponse>> getAllRoutinesByMember(@RequestParam Long memberId) {
        List<GetAllRoutinesByMemberIdResponse> response = getAllRoutinesByMemberIdUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }
}