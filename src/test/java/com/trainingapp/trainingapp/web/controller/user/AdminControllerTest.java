package com.trainingapp.trainingapp.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.user.admin.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import com.trainingapp.trainingapp.web.dto.user.admin.UpdateAdminRequest;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterAdminUseCase registerAdminUseCase;
    @MockitoBean private GetAdminByIdUseCase getAdminByIdUseCase;
    @MockitoBean private GetAllAdminsByGymIdUseCase getAllAdminsByGymIdUseCase;
    @MockitoBean private UpdateAdminUseCase updateAdminUseCase;
    @MockitoBean private DeleteAdminUseCase deleteAdminUseCase;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("POST /admins - Debería retornar 201 al registrar")
    void shouldRegisterAndReturn201() throws Exception {
        mockMvc.perform(post("/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /admins/{id} - Debería retornar 200")
    void shouldGetByIdAndReturn200() throws Exception {
        when(getAdminByIdUseCase.execute(1L)).thenReturn(mock(AdminResponse.class));
        mockMvc.perform(get("/admins/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("GET /admins?gymId={id} - Debería retornar 200")
    void shouldGetAllByGymIdAndReturn200() throws Exception {
        when(getAllAdminsByGymIdUseCase.execute(10L)).thenReturn(List.of());
        mockMvc.perform(get("/admins").param("gymId", "10")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("PUT /admins/{id} - Debería retornar 200")
    void shouldUpdateAndReturn200() throws Exception {
        mockMvc.perform(put("/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("DELETE /admins/{id} - Debería retornar 204")
    void shouldDeleteAndReturn204() throws Exception {
        doNothing().when(deleteAdminUseCase).execute(1L);
        mockMvc.perform(delete("/admins/1")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "TRAINER") // Usuario no autorizado
    @DisplayName("DELETE /admins/{id} - Debería retornar 403 Forbidden para rol incorrecto")
    void shouldReturn403ForUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/admins/1")).andExpect(status().isForbidden());
    }
}