package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.useCase.user.receptionist.*;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import com.trainingapp.trainingapp.web.dto.user.receptionist.RegisterReceptionistRequest;
import com.trainingapp.trainingapp.web.dto.user.receptionist.UpdateReceptionistRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receptionists")
public class ReceptionistController {
    private final RegisterReceptionistUseCase registerReceptionistUseCase;
    private final GetReceptionistByIdUseCase getReceptionistByIdUseCase;
    private final GetAllReceptionistsByGymIdUseCase getAllReceptionistsByGymIdUseCase;
    private final UpdateReceptionistUseCase updateReceptionistUseCase;
    private final DeleteReceptionistUseCase deleteReceptionistUseCase;

    public ReceptionistController(RegisterReceptionistUseCase registerReceptionistUseCase,
                                  GetReceptionistByIdUseCase getReceptionistByIdUseCase,
                                  GetAllReceptionistsByGymIdUseCase getAllReceptionistsByGymIdUseCase,
                                  UpdateReceptionistUseCase updateReceptionistUseCase,
                                  DeleteReceptionistUseCase deleteReceptionistUseCase) {
        this.registerReceptionistUseCase = registerReceptionistUseCase;
        this.getReceptionistByIdUseCase = getReceptionistByIdUseCase;
        this.getAllReceptionistsByGymIdUseCase = getAllReceptionistsByGymIdUseCase;
        this.updateReceptionistUseCase = updateReceptionistUseCase;
        this.deleteReceptionistUseCase = deleteReceptionistUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    public ResponseEntity<ReceptionistResponse> register(
            @RequestBody @Valid RegisterReceptionistRequest request) {
        ReceptionistResponse response = registerReceptionistUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ReceptionistResponse> getReceptionistById(@PathVariable Long id) {
        // Un recepcionista puede ver su propio perfil, y el admin puede ver cualquiera
        ReceptionistResponse response = getReceptionistByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/gym/{gymId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReceptionistResponse>> getAllReceptionistsByGymId(@PathVariable Long gymId) {
        List<ReceptionistResponse> responses = getAllReceptionistsByGymIdUseCase.execute(gymId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReceptionistResponse> updateReceptionist(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReceptionistRequest request) {
        ReceptionistResponse response = updateReceptionistUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReceptionist(@PathVariable Long id) {
        deleteReceptionistUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
