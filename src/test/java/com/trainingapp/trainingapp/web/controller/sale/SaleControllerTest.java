package com.trainingapp.trainingapp.web.controller.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.sale.GetSaleByIdUseCase;
import com.trainingapp.trainingapp.application.useCase.sale.ProcessSaleUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.sale.CreateSaleRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleDetailRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProcessSaleUseCase processSaleUseCase;
    @MockitoBean
    private GetSaleByIdUseCase getSaleByIdUseCase;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /sales - Debería procesar la venta y retornar 201 con el monto total")
    void shouldReturn201_WhenProcessingSale() throws Exception {
        // Arrange
        SaleDetailRequest detail = new SaleDetailRequest(5L, 2);
        CreateSaleRequest request =
                new CreateSaleRequest(100L, PaymentMethod.CASH, List.of(detail));

        SaleResponse fakeResponse =
                new SaleResponse(1L, LocalDateTime.now(), new BigDecimal("10.00"),
                        PaymentMethod.CASH, 10L, 2L, 100L, List.of());

        when(processSaleUseCase.execute(any(CreateSaleRequest.class))).thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /sales/{id} - Debería retornar 200 con la información de la venta")
    void shouldReturn200_WhenGettingSaleById() throws Exception {
        // Arrange
        SaleResponse fakeResponse =
                new SaleResponse(1L, LocalDateTime.now(), new BigDecimal("15.50"),
                        PaymentMethod.CARD, 10L, 1L, 100L, List.of());

        when(getSaleByIdUseCase.execute(1L)).thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(15.50))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"));
    }
}