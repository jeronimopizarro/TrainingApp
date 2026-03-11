package com.trainingapp.trainingapp.web.controller.gym;

import com.trainingapp.trainingapp.application.usecase.gym.*;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gyms")
public class GymController {

    private final CreateGymUseCase createGymUseCase;
    private final GetGymByIdUseCase getGymByIdUseCase;
    private final GetAllGymsUseCase  getAllGymsUseCase;
    private final UpdateGymUseCase updateGymUseCase;
    private final DeleteGymUseCase deleteGymUseCase;

    public GymController(CreateGymUseCase createGymUseCase, GetGymByIdUseCase getGymByIdUseCase,
                         GetAllGymsUseCase getAllGymsUseCase, UpdateGymUseCase updateGymUseCase,
                         DeleteGymUseCase deleteGymUseCase) {
        this.createGymUseCase = createGymUseCase;
        this.getGymByIdUseCase = getGymByIdUseCase;
        this.getAllGymsUseCase = getAllGymsUseCase;
        this.updateGymUseCase = updateGymUseCase;
        this.deleteGymUseCase = deleteGymUseCase;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<GymResponse> createGym(@RequestBody CreateGymRequest createGymRequest) {
        GymResponse response = createGymUseCase.execute(createGymRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<GymResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getGymByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<GymResponse>> getAll() {
        return ResponseEntity.ok(getAllGymsUseCase.execute());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GymResponse> updateGym(@PathVariable Long id, @RequestBody UpdateGymRequest request) {
        GymResponse response = updateGymUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGym(@PathVariable Long id) {
        deleteGymUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}