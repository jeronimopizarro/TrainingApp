package com.trainingapp.trainingapp.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.user.trainer.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterTrainerUseCase registerTrainerUseCase;
    @MockitoBean
    private GetTrainerByIdUseCase getTrainerByIdUseCase;
    @MockitoBean
    private GetAllTrainersByGymIdUseCase getAllTrainersByGymIdUseCase;
    @MockitoBean
    private UpdateTrainerUseCase updateTrainerUseCase;
    @MockitoBean
    private DeleteTrainerUseCase deleteTrainerUseCase;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("POST /trainers - Debería retornar 201 al registrar")
    void shouldRegisterAndReturn201() throws Exception {
        // JSON de entrada genérico para que pase por el controlador correctamente
        String validJsonRequest =
                "{\"firstName\":\"Entrenador\", \"lastName\":\"Test\", \"email\":\"trainer@test.com\", \"password\":\"pass123\", \"dni\":\"12345678\", \"gymId\":10, \"specialization\":\"Musculación\"}";

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("GET /trainers/{id} - Debería retornar 200")
    void shouldGetByIdAndReturn200() throws Exception {
        mockMvc.perform(get("/trainers/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /trainers?gymId={id} - Debería retornar 200")
    void shouldGetAllByGymIdAndReturn200() throws Exception {
        when(getAllTrainersByGymIdUseCase.execute(10L)).thenReturn(List.of());
        mockMvc.perform(get("/trainers").param("gymId", "10")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("PUT /trainers/{id} - Debería retornar 200 al actualizar")
    void shouldUpdateAndReturn200() throws Exception {
        // JSON de entrada genérico para que pase por el controlador correctamente
        String validJsonRequest =
                "{\"firstName\":\"Nuevo\", \"lastName\":\"Nombre\", \"dni\":\"87654321\", \"specialization\":\"Cardio\"}";

        mockMvc.perform(put("/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("DELETE /trainers/{id} - Debería retornar 204")
    void shouldDeleteAndReturn204() throws Exception {
        doNothing().when(deleteTrainerUseCase).execute(1L);
        mockMvc.perform(delete("/trainers/1")).andExpect(status().isNoContent());
    }
}