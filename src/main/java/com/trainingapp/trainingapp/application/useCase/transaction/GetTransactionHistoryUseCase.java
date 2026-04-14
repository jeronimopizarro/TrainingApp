package com.trainingapp.trainingapp.application.useCase.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction.TransactionJpaRepository;
import com.trainingapp.trainingapp.web.dto.transaction.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetTransactionHistoryUseCase {

    private final TransactionJpaRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public GetTransactionHistoryUseCase(TransactionJpaRepository transactionRepository, SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(TransactionCategory category) {
        Long gymId = securityUtils.getCurrentUserGymId();
        
        List<TransactionJpaEntity> transactions = category == null 
            ? transactionRepository.findAllByGymIdOrderByTransactionDateDesc(gymId)
            : transactionRepository.findAllByGymIdAndCategoryOrderByTransactionDateDesc(gymId, category);

        return transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getAmount(),
                        t.getTransactionDate(),
                        t.getPaymentMethod(),
                        t.getCategory(),
                        t.getNotes(),
                        t.getGymId(),
                        t.getRegisteredByAdminId(),
                        t.getSubscriptionId(),
                        t.getSaleId()
                ))
                .collect(Collectors.toList());
    }
}
