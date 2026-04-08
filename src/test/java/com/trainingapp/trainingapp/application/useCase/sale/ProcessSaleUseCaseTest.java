package com.trainingapp.trainingapp.application.useCase.sale;

import com.trainingapp.trainingapp.application.mapper.sale.SaleDTOMapper;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionCommand;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionUseCase;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.exception.product.InsufficientStockException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.domain.repository.sale.SaleRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.sale.CreateSaleRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleDetailRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessSaleUseCaseTest {

    @Mock private SaleRepository saleRepository;
    @Mock private ProductRepository productRepository;
    @Mock private RegisterTransactionUseCase registerTransactionUseCase;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymValidator gymValidator;
    @Mock private SaleDTOMapper saleDTOMapper;

    @InjectMocks private ProcessSaleUseCase useCase;

    @Test
    @DisplayName("Debería procesar una venta, restar stock y registrar la transacción contable")
    void shouldProcessSaleAndRegisterTransaction() {
        // Simular admin logueado
        User mockAdmin = mock(User.class);
        when(mockAdmin.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockAdmin);
        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        doNothing().when(gymValidator).validateExists(10L);

        // Simular Producto con stock de 10
        Product product = Product.restore(5L, "Gatorade", "Bebida", new BigDecimal("5.00"), 10, "", 10L, true);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        doNothing().when(securityUtils).validateSameGym(10L);

        // Simulamos respuesta del repo y del mapper para evitar Nulos
        Sale fakeSale = mock(Sale.class);
        when(fakeSale.getId()).thenReturn(99L);
        when(fakeSale.getTotalAmount()).thenReturn(new BigDecimal("10.00")); // 2 Gatorades
        when(fakeSale.getPaymentMethod()).thenReturn(PaymentMethod.CARD);
        when(saleRepository.save(any(Sale.class))).thenReturn(fakeSale);

        CreateSaleRequest request = new CreateSaleRequest(
                100L, PaymentMethod.CARD, List.of(new SaleDetailRequest(5L, 2))
                );
        useCase.execute(request);

        verify(productRepository).save(product); // Debe actualizar el stock
        assertEquals(8, product.getStock(), "El stock del producto debió bajar de 10 a 8");

        verify(saleRepository).save(any(Sale.class)); // Debe guardar la venta

        // ¿Mandó la orden al caso de uso de Transacciones?
        verify(registerTransactionUseCase).execute(any(RegisterTransactionCommand.class));
    }

    @Test
    @DisplayName("Debería abortar toda la venta si un producto no tiene stock suficiente")
    void shouldAbortSale_WhenStockIsInsufficient() {
        User mockAdmin = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockAdmin);
        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);

        // Producto con solo 1 en stock
        Product product = Product.restore(5L, "Gatorade", "Bebida", new BigDecimal("5.00"), 1, "", 10L, true);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        // Intentamos comprar 5
        CreateSaleRequest request = new CreateSaleRequest(100L,
                PaymentMethod.CASH, List.of(new SaleDetailRequest(5L, 5)));

        assertThrows(InsufficientStockException.class, () -> useCase.execute(request));

        // Verificamos que NO se haya guardado nada en DB si explotó
        verify(saleRepository, never()).save(any());
        verify(registerTransactionUseCase, never()).execute(any());
    }
}