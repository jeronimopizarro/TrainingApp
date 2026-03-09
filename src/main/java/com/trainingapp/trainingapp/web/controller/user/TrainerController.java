package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.RegisterTrainerUseCase;
import com.trainingapp.trainingapp.web.dto.user.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.TrainerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final RegisterTrainerUseCase registerTrainerUseCase;

    public TrainerController(RegisterTrainerUseCase registerTrainerUseCase) {
        this.registerTrainerUseCase = registerTrainerUseCase;
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> register(@RequestBody RegisterTrainerRequest request) {
        TrainerResponse response = registerTrainerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}