package com.trainingapp.trainingapp.domain.entity.product;

import lombok.Getter;

@Getter
public class Product {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private boolean active;
    private Long gymId;

    public Product(Long id, String name, String description, Double price, Integer stock,
                   String imageUrl, boolean active, Long gymId) {
        validateProductData(name, price, stock, gymId);

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.active = active;
        this.gymId = gymId;
    }

    public static Product createNew(String name, String description, Double price, Integer stock,
                                    String imageUrl, Long gymId) {
        return new Product(null, name, description, price, stock, imageUrl, true, gymId);
    }

    private void validateProductData(String name, Double price, Integer stock, Long gymId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo ni nulo.");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo ni nulo.");
        }
        if (gymId == null) {
            throw new IllegalArgumentException("El producto debe pertenecer a un gimnasio.");
        }
    }

    public boolean hasEnoughStock(int quantity) {
        return this.stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException(
                    "Stock insuficiente para el producto: " + this.name + ". Stock actual: " + this.stock);
        }
        this.stock -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a cero.");
        }
        this.stock += quantity;
    }

    public void updatePrice(Double newPrice) {
        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("El nuevo precio no puede ser negativo ni nulo.");
        }
        this.price = newPrice;
    }

    public void updateDetails(String name, String description, String imageUrl) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}