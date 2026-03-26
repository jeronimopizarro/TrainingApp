package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, Long> {

    List<TransactionJpaEntity> findAllByGymIdOrderByTransactionDateDesc(Long gymId);
}