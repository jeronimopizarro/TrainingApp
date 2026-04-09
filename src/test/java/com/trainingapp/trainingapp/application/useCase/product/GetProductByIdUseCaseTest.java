package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @Mock private ProductRepository productRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ProductDTOMapper productDTOMapper;

    @InjectMocks private GetProductByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar el producto si pertenece al mismo gimnasio")
    void shouldReturnProductById() {
        Long productId = 1L;
        Long gymId = 10L;
        Product mockProduct = mock(Product.class);
        ProductResponse mockResponse = mock(ProductResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);

        when(productDTOMapper.toResponse(mockProduct)).thenReturn(mockResponse);

        ProductResponse response = useCase.execute(productId);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedGymAccessException si el producto es de otro gimnasio")
    void shouldThrowExceptionWhenGymMismatch() {
        Long productId = 1L;
        Long otherGymId = 99L;
        Product mockProduct = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getGymId()).thenReturn(otherGymId);

        doThrow(new com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException())
                .when(securityUtils).validateSameGym(otherGymId);

        assertThrows(com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException.class,
                () -> useCase.execute(productId));

        verify(securityUtils).validateSameGym(otherGymId);
        verifyNoInteractions(productDTOMapper);
    }
}