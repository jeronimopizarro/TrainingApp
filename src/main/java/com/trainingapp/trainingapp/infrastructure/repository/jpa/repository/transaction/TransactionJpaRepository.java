package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction;

import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.transaction.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, Long> {

    List<TransactionJpaEntity> findAllByGymIdOrderByTransactionDateDesc(Long gymId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
            "WHERE t.gymId = :gymId " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate")
    BigDecimal sumRevenueByDateRange(@Param("gymId") Long gymId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
            "WHERE t.gymId = :gymId " +
            "AND t.category = :category " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate")
    BigDecimal sumRevenueByCategoryAndDateRange(@Param("gymId") Long gymId,
                                                @Param("category") TransactionCategory category,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}