package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.RegisterAdminUseCase;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final RegisterAdminUseCase registerAdminUseCase;

    public AdminController(RegisterAdminUseCase registerAdminUseCase) {
        this.registerAdminUseCase = registerAdminUseCase;
    }

    @PostMapping
    public ResponseEntity<AdminResponse> register(@RequestBody RegisterAdminRequest request) {
        AdminResponse response = registerAdminUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}