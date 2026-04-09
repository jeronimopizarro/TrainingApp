package com.trainingapp.trainingapp.web.controller.membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.membership.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembershipPlanController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class MembershipPlanControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateMembershipPlanUseCase createUseCase;
    @MockitoBean private UpdateMembershipPlanUseCase updateUseCase;
    @MockitoBean private DeleteMembershipPlanUseCase deleteUseCase;
    @MockitoBean private GetAllMembershipPlansByGymIdUseCase getAllUseCase;

    // Dependencias de seguridad
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("POST /membership-plans - Debería retornar 201 Created")
    void shouldCreatePlan() throws Exception {
        CreateMembershipPlanRequest request = new CreateMembershipPlanRequest(
                "Base", "Plan Base", new BigDecimal("100"), 1, 10L
        );
        MembershipPlanResponse mockResponse = new MembershipPlanResponse(
                1L, "Base", "Plan Base", new BigDecimal("100"), 1, 10L, true
        );

        when(createUseCase.execute(any(CreateMembershipPlanRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Base"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("PUT /membership-plans/{id} - Debería retornar 200 OK")
    void shouldUpdatePlan() throws Exception {
        UpdateMembershipPlanRequest request = new UpdateMembershipPlanRequest(
                "Pro", "Plan Pro", new BigDecimal("200"), 3
        );
        MembershipPlanResponse mockResponse = new MembershipPlanResponse(
                1L, "Pro", "Plan Pro", new BigDecimal("200"), 3, 10L, true
        );

        when(updateUseCase.execute(eq(1L), any(UpdateMembershipPlanRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/membership-plans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pro"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("DELETE /membership-plans/{id} - Debería retornar 204 No Content")
    void shouldDeletePlan() throws Exception {
        doNothing().when(deleteUseCase).execute(1L);

        mockMvc.perform(delete("/membership-plans/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /membership-plans?gymId={gymId} - Debería retornar 200 OK")
    void shouldGetAllPlansByGymId() throws Exception {
        when(getAllUseCase.execute(10L)).thenReturn(List.of());

        mockMvc.perform(get("/membership-plans")
                        .param("gymId", "10"))
                .andExpect(status().isOk());
    }
}