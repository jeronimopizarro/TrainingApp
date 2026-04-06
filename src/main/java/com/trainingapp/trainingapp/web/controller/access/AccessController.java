package com.trainingapp.trainingapp.web.controller.access;

import com.trainingapp.trainingapp.application.useCase.access.GenerateAccessQrUseCase;
import com.trainingapp.trainingapp.application.useCase.access.ValidateAccessUseCase;
import com.trainingapp.trainingapp.web.dto.access.QrTokenResponse;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessRequest;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/access")
public class AccessController {

    private final GenerateAccessQrUseCase generateAccessQrUseCase;
    private final ValidateAccessUseCase validateAccessUseCase;

    public AccessController(GenerateAccessQrUseCase generateAccessQrUseCase,
                            ValidateAccessUseCase validateAccessUseCase) {
        this.generateAccessQrUseCase = generateAccessQrUseCase;
        this.validateAccessUseCase = validateAccessUseCase;
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/qr")
    public ResponseEntity<QrTokenResponse> generateQr(@RequestParam Long memberId) {
        QrTokenResponse response = generateAccessQrUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    @PostMapping("/validate")
    public ResponseEntity<ValidateAccessResponse> validateAccess(@Valid @RequestBody ValidateAccessRequest request) {
        ValidateAccessResponse response = validateAccessUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}