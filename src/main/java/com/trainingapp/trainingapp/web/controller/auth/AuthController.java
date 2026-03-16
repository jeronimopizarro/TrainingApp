package com.trainingapp.trainingapp.web.controller.auth;

import com.trainingapp.trainingapp.application.useCase.auth.LoginUseCase;
import com.trainingapp.trainingapp.web.dto.auth.AuthResponse;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }
}