package com.trainingapp.trainingapp.domain.entity.product;

import com.trainingapp.trainingapp.domain.exception.product.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Product {

    private final Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private final Long gymId;
    private boolean active;

    private Product(Long id, String name, String description, BigDecimal price, Integer stock, String imageUrl, Long gymId, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.gymId = gymId;
        this.active = active;
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new ProductNameRequiredException();
        }
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeProductPriceException();
        }
        if (this.stock == null || this.stock < 0) {
            throw new NegativeProductStockException();
        }
    }

    public static Product createNew(String name, String description, BigDecimal price, Integer stock, String imageUrl, Long gymId) {
        return new Product(null, name, description, price, stock, imageUrl, gymId, true);
    }

    public static Product restore(Long id, String name, String description, BigDecimal price, Integer stock, String imageUrl, Long gymId, boolean active) {
        return new Product(id, name, description, price, stock, imageUrl, gymId, active);
    }

    public void reduceStock(Integer quantityToReduce) {
        if (quantityToReduce == null || quantityToReduce <= 0) {
            throw new InvalidStockOperationException();
        }
        if (this.stock < quantityToReduce) {
            throw new InsufficientStockException(this.name, this.stock);
        }
        this.stock -= quantityToReduce;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockOperationException();
        }
        this.stock += quantity;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeProductPriceException();
        }
        this.price = newPrice;
    }

    public void updateDetails(String name, String description, String imageUrl) {
        if (name == null || name.trim().isEmpty()) {
            throw new ProductNameRequiredException();
        }
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void deactivate() {
        if (!this.active) {
            throw new ProductAlreadyInactiveException();
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new ProductAlreadyActiveException();
        }
        this.active = true;
    }
}