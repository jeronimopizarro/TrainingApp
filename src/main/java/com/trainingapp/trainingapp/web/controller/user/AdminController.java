package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.admin.*;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import com.trainingapp.trainingapp.web.dto.user.admin.UpdateAdminRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final RegisterAdminUseCase registerAdminUseCase;
    private final GetAdminByIdUseCase getAdminByIdUseCase;
    private final GetAllAdminsByGymIdUseCase getAllAdminsByGymIdUseCase;
    private final UpdateAdminUseCase updateAdminUseCase;
    private final DeleteAdminUseCase deleteAdminUseCase;

    public AdminController(RegisterAdminUseCase registerAdminUseCase,
                           GetAdminByIdUseCase getAdminByIdUseCase,
                           GetAllAdminsByGymIdUseCase getAllAdminsByGymIdUseCase,
                           UpdateAdminUseCase updateAdminUseCase,
                           DeleteAdminUseCase deleteAdminUseCase) {
        this.registerAdminUseCase = registerAdminUseCase;
        this.getAdminByIdUseCase = getAdminByIdUseCase;
        this.getAllAdminsByGymIdUseCase = getAllAdminsByGymIdUseCase;
        this.updateAdminUseCase = updateAdminUseCase;
        this.deleteAdminUseCase = deleteAdminUseCase;
    }

    @PostMapping
    public ResponseEntity<AdminResponse> register(@RequestBody RegisterAdminRequest request) {
        AdminResponse response = registerAdminUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getAdminByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllByGymId(@RequestParam Long gymId) {
        return ResponseEntity.ok(getAllAdminsByGymIdUseCase.execute(gymId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> update(@PathVariable Long id, @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(updateAdminUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteAdminUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}