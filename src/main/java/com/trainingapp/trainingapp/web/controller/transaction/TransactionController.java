package com.trainingapp.trainingapp.web.controller.transaction;

import com.trainingapp.trainingapp.application.useCase.transaction.GetTransactionHistoryUseCase;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.web.dto.transaction.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    public TransactionController(GetTransactionHistoryUseCase getTransactionHistoryUseCase) {
        this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @RequestParam(required = false) TransactionCategory category) {
        return ResponseEntity.ok(getTransactionHistoryUseCase.execute(category));
    }
}
