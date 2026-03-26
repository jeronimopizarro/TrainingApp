package com.trainingapp.trainingapp.domain.repository.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAllByGymId(Long gymId);

    List<Product> searchByName(Long gymId, String name);
}