package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock private ProductRepository productRepository;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private DeleteProductUseCase useCase;

    @Test
    @DisplayName("Debería desactivar (borrado lógico) un producto exitosamente")
    void shouldDeleteProductSuccessfully() {
        Long productId = 1L;
        Long gymId = 10L;
        Product mockProduct = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);

        useCase.execute(productId);

        verify(mockProduct).deactivate(); // Borrado lógico
        verify(productRepository).save(mockProduct); // Persistencia
        verify(securityUtils).validateSameGym(gymId);
    }
}