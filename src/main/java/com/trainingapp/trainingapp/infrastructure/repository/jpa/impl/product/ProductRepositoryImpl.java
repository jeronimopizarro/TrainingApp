package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.product.ProductJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.product.ProductMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.product.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository, ProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAllByGymId(Long gymId) {
        return jpaRepository.findAllByGymIdAndActiveTrue(gymId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStockRange(Long gymId, int min, int max) {
        return jpaRepository.findByGymIdAndActiveTrueAndStockBetween(gymId, min, max)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findWithNoStock(Long gymId) {
        return jpaRepository.findByGymIdAndActiveTrueAndStockLessThanEqual(gymId, 0)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByName(Long gymId, String name) {
        return jpaRepository.findByGymIdAndNameContainingIgnoreCaseAndActiveTrue(gymId, name)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}