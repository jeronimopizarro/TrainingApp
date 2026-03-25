package com.trainingapp.trainingapp.domain.repository.sale;

import com.trainingapp.trainingapp.domain.entity.sale.Sale;

import java.util.List;
import java.util.Optional;

public interface SaleRepository {
    Sale save(Sale sale);
    Optional<Sale> findById(Long id);
    List<Sale> findAllByGymId(Long gymId);
}