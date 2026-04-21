package com.trainingapp.trainingapp.web.controller.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("Servidor de TrainingApp Activo y Funcionando 🚀");
    }
}