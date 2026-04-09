package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllProductsByGymIdUseCaseTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductDTOMapper productDTOMapper;
    @Mock private GymValidator gymValidator;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private GetAllProductsByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar todos los productos de un gimnasio específico")
    void shouldReturnAllProducts() {
        Long gymId = 10L;

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        Product mockProduct = mock(Product.class);
        when(productRepository.findAllByGymId(gymId)).thenReturn(List.of(mockProduct));

        ProductResponse mockResponse = mock(ProductResponse.class);
        when(productDTOMapper.toResponse(mockProduct)).thenReturn(mockResponse);

        List<ProductResponse> result = useCase.execute(gymId);

        assertEquals(1, result.size());
        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
        verify(productRepository).findAllByGymId(gymId);
    }
}