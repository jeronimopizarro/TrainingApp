package com.trainingapp.trainingapp.web.controller.sale;

import com.trainingapp.trainingapp.application.useCase.sale.ProcessSaleUseCase;
import com.trainingapp.trainingapp.web.dto.sale.CreateSaleRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final ProcessSaleUseCase processSaleUseCase;

    public SaleController(ProcessSaleUseCase processSaleUseCase) {
        this.processSaleUseCase = processSaleUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    public ResponseEntity<SaleResponse> processSale(@Valid @RequestBody CreateSaleRequest request) {
        SaleResponse response = processSaleUseCase.execute(request);
        return buildCreatedResponse(response);
    }

    private ResponseEntity<SaleResponse> buildCreatedResponse(SaleResponse response) {
        URI location = URI.create("/sales/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
}