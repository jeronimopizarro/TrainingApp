package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.transaction;

import com.trainingapp.trainingapp.domain.entity.transaction.Transaction;
import com.trainingapp.trainingapp.domain.repository.transaction.TransactionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.transaction.TransactionMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction.TransactionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper mapper;

    public TransactionRepositoryImpl(TransactionJpaRepository jpaRepository, TransactionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = mapper.toEntity(transaction);
        TransactionJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findAllByGymId(Long gymId) {
        return jpaRepository.findAllByGymIdOrderByTransactionDateDesc(gymId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}