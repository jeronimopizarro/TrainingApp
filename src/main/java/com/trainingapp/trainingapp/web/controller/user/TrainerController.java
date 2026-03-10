package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.trainer.*;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final RegisterTrainerUseCase registerTrainerUseCase;
    private final GetTrainerByIdUseCase getTrainerByIdUseCase;
    private final GetAllTrainersByGymIdUseCase getAllTrainersByGymIdUseCase;
    private final UpdateTrainerUseCase updateTrainerUseCase;
    private final DeleteTrainerUseCase deleteTrainerUseCase;

    public TrainerController(RegisterTrainerUseCase registerTrainerUseCase,
                             GetTrainerByIdUseCase getTrainerByIdUseCase,
                             GetAllTrainersByGymIdUseCase getAllTrainersByGymIdUseCase,
                             UpdateTrainerUseCase updateTrainerUseCase,
                             DeleteTrainerUseCase deleteTrainerUseCase) {
        this.registerTrainerUseCase = registerTrainerUseCase;
        this.getTrainerByIdUseCase = getTrainerByIdUseCase;
        this.getAllTrainersByGymIdUseCase = getAllTrainersByGymIdUseCase;
        this.updateTrainerUseCase = updateTrainerUseCase;
        this.deleteTrainerUseCase = deleteTrainerUseCase;
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> register(@RequestBody RegisterTrainerRequest request) {
        TrainerResponse response = registerTrainerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getTrainerByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllByGymId(@RequestParam Long gymId) {
        return ResponseEntity.ok(getAllTrainersByGymIdUseCase.execute(gymId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponse> update(@PathVariable Long id, @RequestBody UpdateTrainerRequest request) {
        return ResponseEntity.ok(updateTrainerUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteTrainerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}