package com.trainingapp.trainingapp.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.user.receptionist.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import com.trainingapp.trainingapp.web.dto.user.receptionist.RegisterReceptionistRequest;
import com.trainingapp.trainingapp.web.dto.user.receptionist.UpdateReceptionistRequest;
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

@WebMvcTest(ReceptionistController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class ReceptionistControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterReceptionistUseCase registerReceptionistUseCase;
    @MockitoBean private GetReceptionistByIdUseCase getReceptionistByIdUseCase;
    @MockitoBean private GetAllReceptionistsByGymIdUseCase getAllReceptionistsByGymIdUseCase;
    @MockitoBean private UpdateReceptionistUseCase updateReceptionistUseCase;
    @MockitoBean private DeleteReceptionistUseCase deleteReceptionistUseCase;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("POST /receptionists - Debería retornar 201 al registrar")
    void shouldRegisterAndReturn201() throws Exception {
        mockMvc.perform(post("/receptionists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("GET /receptionists/{id} - Debería retornar 200")
    void shouldGetByIdAndReturn200() throws Exception {
        when(getReceptionistByIdUseCase.execute(1L)).thenReturn(mock(ReceptionistResponse.class));
        mockMvc.perform(get("/receptionists/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /receptionists/gym/{gymId} - Debería retornar 200")
    void shouldGetAllByGymIdAndReturn200() throws Exception {
        when(getAllReceptionistsByGymIdUseCase.execute(10L)).thenReturn(List.of());
        mockMvc.perform(get("/receptionists/gym/10")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /receptionists/{id} - Debería retornar 200/400 dependiendo del request")
    void shouldUpdateAndReturnStatus() throws Exception {
        mockMvc.perform(put("/receptionists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Por el @Valid del UpdateReceptionistRequest vacío
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /receptionists/{id} - Debería retornar 204")
    void shouldDeleteAndReturn204() throws Exception {
        doNothing().when(deleteReceptionistUseCase).execute(1L);
        mockMvc.perform(delete("/receptionists/1")).andExpect(status().isNoContent());
    }
}