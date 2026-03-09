package com.trainingapp.trainingapp.web.controller.gym;

import com.trainingapp.trainingapp.application.usecase.gym.CreateGymUseCase;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gyms")
public class GymController {

    private final CreateGymUseCase createGymUseCase;

    public GymController(CreateGymUseCase createGymUseCase) {
        this.createGymUseCase = createGymUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateGymResponse> createGym(@RequestBody CreateGymRequest createGymRequest) {
        CreateGymResponse response = createGymUseCase.execute(createGymRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}