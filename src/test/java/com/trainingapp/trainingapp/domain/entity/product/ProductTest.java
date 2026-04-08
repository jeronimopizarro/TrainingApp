package com.trainingapp.trainingapp.domain.entity.product;

import com.trainingapp.trainingapp.domain.exception.product.InsufficientStockException;
import com.trainingapp.trainingapp.domain.exception.product.InvalidStockOperationException;
import com.trainingapp.trainingapp.domain.exception.product.NegativeProductPriceException;
import com.trainingapp.trainingapp.domain.exception.product.NegativeProductStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product createValidProduct(int stock) {
        return Product.restore(1L, "Proteína Whey", "5lbs", new BigDecimal("50.00"), stock, "url", 10L, true);
    }

    @Nested
    @DisplayName("Gestión de Stock (reduceStock)")
    class StockManagement {

        @Test
        @DisplayName("Debería restar el stock correctamente si hay cantidad suficiente")
        void shouldReduceStock_WhenSufficientQuantity() {
            Product product = createValidProduct(10);
            product.reduceStock(3);
            assertEquals(7, product.getStock());
        }

        @Test
        @DisplayName("Debería lanzar InsufficientStockException si se intenta vender más de lo que hay")
        void shouldThrowException_WhenStockIsInsufficient() {
            Product product = createValidProduct(2); // Solo quedan 2
            assertThrows(InsufficientStockException.class, () -> product.reduceStock(5));
        }

        @Test
        @DisplayName("Debería lanzar InvalidStockOperationException si se intenta restar 0 o negativo")
        void shouldThrowException_WhenReducingNegativeQuantity() {
            Product product = createValidProduct(10);
            assertThrows(InvalidStockOperationException.class, () -> product.reduceStock(0));
            assertThrows(InvalidStockOperationException.class, () -> product.reduceStock(-5));
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class Validations {

        @Test
        @DisplayName("Debería lanzar error si el precio configurado es negativo")
        void shouldThrowException_WhenPriceIsNegative() {
            assertThrows(NegativeProductPriceException.class, () ->
                    Product.createNew("Pre-Entreno", "Polvo", new BigDecimal("-10.00"), 5, "url", 10L)
            );
        }

        @Test
        @DisplayName("Debería lanzar error si el stock inicial es negativo")
        void shouldThrowException_WhenStockIsNegative() {
            assertThrows(NegativeProductStockException.class, () ->
                    Product.createNew("Creatina", "Polvo", new BigDecimal("20.00"), -1, "url", 10L)
            );
        }
    }
}