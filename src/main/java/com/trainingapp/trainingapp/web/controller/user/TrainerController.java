package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.trainer.*;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PostMapping
    public ResponseEntity<TrainerResponse> register(@RequestBody RegisterTrainerRequest request) {
        TrainerResponse response = registerTrainerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getTrainerByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllByGymId(@RequestParam Long gymId) {
        return ResponseEntity.ok(getAllTrainersByGymIdUseCase.execute(gymId));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponse> update(@PathVariable Long id, @RequestBody UpdateTrainerRequest request) {
        return ResponseEntity.ok(updateTrainerUseCase.execute(id, request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteTrainerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}