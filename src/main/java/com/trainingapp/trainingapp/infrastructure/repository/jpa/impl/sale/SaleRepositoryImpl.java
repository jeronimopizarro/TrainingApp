package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.sale;

import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.repository.sale.SaleRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.sale.SaleMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale.SaleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SaleRepositoryImpl implements SaleRepository {

    private final SaleJpaRepository jpaRepository;
    private final SaleMapper mapper;

    public SaleRepositoryImpl(SaleJpaRepository jpaRepository, SaleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Sale save(Sale sale) {
        SaleJpaEntity entity = mapper.toEntity(sale);
        SaleJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Sale> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Sale> findAllByGymId(Long gymId) {
        return jpaRepository.findAllByGymIdOrderBySaleDateDesc(gymId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}