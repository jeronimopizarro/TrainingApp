package com.trainingapp.trainingapp.web.controller.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.subscription.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class SubscriptionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateSubscriptionUseCase createSubscriptionUseCase;
    @MockitoBean private CancelSubscriptionUseCase cancelSubscriptionUseCase;
    @MockitoBean private GetActiveSubscriptionByMemberUseCase getActiveSubscriptionByMemberUseCase;
    @MockitoBean private GetAllSubscriptionsByMemberUseCase getAllSubscriptionsByMemberUseCase;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    // 1. POST (Crear)
    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /subscriptions - Debería retornar 201 al crear suscripción")
    void shouldReturn201WhenCreatingSubscription() throws Exception {
        // CAMBIO: Agregamos PaymentMethod.CASH al request
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                100L, 1L, LocalDate.now(), PaymentMethod.CASH
        );

        // CAMBIO: Agregamos los campos remainingDays (30) e isActive (true) al response
        SubscriptionResponse response = new SubscriptionResponse(
                1L, 100L, 1L, "Pase Libre", LocalDate.now(), LocalDate.now().plusMonths(1),
                SubscriptionStatus.ACTIVE, 30, true
        );

        when(createSubscriptionUseCase.execute(any(CreateSubscriptionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isActive").value(true)); // Validamos el nuevo campo
    }

    // 2. GET /active (Consultar Activa)
    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /subscriptions/active - Debería retornar 200 y la suscripción actual")
    void shouldReturn200WhenGettingActive() throws Exception {
        // CAMBIO: Agregamos remainingDays e isActive
        SubscriptionResponse response = new SubscriptionResponse(
                1L, 100L, 1L, "Pase Libre", LocalDate.now(), LocalDate.now().plusMonths(1),
                SubscriptionStatus.ACTIVE, 30, true
        );
        when(getActiveSubscriptionByMemberUseCase.execute(100L)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/active").param("memberId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Pase Libre"))
                .andExpect(jsonPath("$.remainingDays").value(30)); // Validamos el nuevo campo
    }

    // 3. GET Historial (Consultar Todas)
    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /subscriptions - Debería retornar 200 y una lista")
    void shouldReturn200WhenGettingAll() throws Exception {
        // CAMBIO: Al estar expirada, remainingDays es 0 e isActive es false
        SubscriptionResponse response = new SubscriptionResponse(
                1L, 100L, 1L, "Mensual", LocalDate.now().minusMonths(1), LocalDate.now(),
                SubscriptionStatus.EXPIRED, 0, false
        );
        when(getAllSubscriptionsByMemberUseCase.execute(100L)).thenReturn(List.of(response));

        mockMvc.perform(get("/subscriptions").param("memberId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isActive").value(false)); // Verificamos que indique inactiva
    }

    // 4. PATCH Cancelar (Cancelar)
    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("PATCH /subscriptions/{id}/cancel - Debería retornar 200 al cancelar")
    void shouldReturn200WhenCancelling() throws Exception {
        // CAMBIO: Al estar cancelada, remainingDays es 0 e isActive es false
        SubscriptionResponse response = new SubscriptionResponse(
                1L, 100L, 1L, "Mensual", LocalDate.now(), LocalDate.now(),
                SubscriptionStatus.CANCELLED, 0, false
        );
        when(cancelSubscriptionUseCase.execute(1L)).thenReturn(response);

        mockMvc.perform(patch("/subscriptions/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.isActive").value(false));
    }
}