package com.trainingapp.trainingapp.web.controller.sale;

import com.trainingapp.trainingapp.application.useCase.sale.GetSaleByIdUseCase;
import com.trainingapp.trainingapp.application.useCase.sale.ProcessSaleUseCase;
import com.trainingapp.trainingapp.web.dto.sale.CreateSaleRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final ProcessSaleUseCase processSaleUseCase;
    private final GetSaleByIdUseCase getSaleByIdUseCase;

    public SaleController(ProcessSaleUseCase processSaleUseCase, GetSaleByIdUseCase getSaleByIdUseCase) {
        this.processSaleUseCase = processSaleUseCase;
        this.getSaleByIdUseCase = getSaleByIdUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<SaleResponse> processSale(@Valid @RequestBody CreateSaleRequest request) {
        SaleResponse response = processSaleUseCase.execute(request);
        return buildCreatedResponse(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(getSaleByIdUseCase.execute(id));
    }

    private ResponseEntity<SaleResponse> buildCreatedResponse(SaleResponse response) {
        URI location = URI.create("/sales/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
}