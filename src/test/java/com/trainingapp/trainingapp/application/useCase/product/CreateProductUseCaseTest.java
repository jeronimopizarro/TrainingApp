package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ProductDTOMapper productDTOMapper;
    @Mock
    private GymValidator gymValidator;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Test
    @DisplayName("Debería crear un producto exitosamente validando el gimnasio y permisos")
    void shouldCreateProductSuccessfully() {
        Long gymId = 10L;
        CreateProductRequest request =
                new CreateProductRequest("Proteína Whey", "1kg Vainilla", new BigDecimal("1500.0"),
                        50, "http://img.url", gymId);

        Product mockProduct = mock(Product.class);
        Product savedProduct = mock(Product.class);
        ProductResponse mockResponse = mock(ProductResponse.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(productDTOMapper.toDomain(request)).thenReturn(mockProduct);
        when(productRepository.save(mockProduct)).thenReturn(savedProduct);
        when(productDTOMapper.toResponse(savedProduct)).thenReturn(mockResponse);

        ProductResponse response = useCase.execute(request);

        assertNotNull(response);

        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
        verify(productRepository).save(mockProduct);
        verify(productDTOMapper).toResponse(savedProduct);
    }
}