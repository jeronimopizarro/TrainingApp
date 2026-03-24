package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.transaction;

import com.trainingapp.trainingapp.domain.entity.transaction.Transaction;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionJpaEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAmount(),
                entity.getTransactionDate(),
                entity.getPaymentMethod(),
                entity.getCategory(),
                entity.getNotes(),
                entity.getGymId(),
                entity.getRegisteredByAdminId(),
                entity.getSubscriptionId(),
                entity.getSaleId()
        );
    }

    public TransactionJpaEntity toEntity(Transaction domain) {
        TransactionJpaEntity entity = new TransactionJpaEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setCategory(domain.getCategory());
        entity.setNotes(domain.getNotes());
        entity.setGymId(domain.getGymId());
        entity.setRegisteredByAdminId(domain.getRegisteredByAdminId());
        entity.setSubscriptionId(domain.getSubscriptionId());
        entity.setSaleId(domain.getSaleId());

        return entity;
    }
}