package com.trainingapp.trainingapp.domain.repository.transaction;

import com.trainingapp.trainingapp.domain.entity.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findAllByGymId(Long gymId);
}