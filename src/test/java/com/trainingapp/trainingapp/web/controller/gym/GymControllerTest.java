package com.trainingapp.trainingapp.web.controller.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.gym.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GymController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class GymControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateGymUseCase createGymUseCase;
    @MockitoBean private GetGymByIdUseCase getGymByIdUseCase;
    @MockitoBean private GetAllGymsUseCase getAllGymsUseCase;
    @MockitoBean private UpdateGymUseCase updateGymUseCase;
    @MockitoBean private DeleteGymUseCase deleteGymUseCase;

    // Dependencias de seguridad
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("POST /gyms - Debería retornar 201 Created")
    void shouldCreateGym() throws Exception {
        CreateGymRequest request = new CreateGymRequest("Gym Test", "Address", "123");
        GymResponse mockResponse = new GymResponse(1L, "Gym Test", "Address", "123", true);

        when(createGymUseCase.execute(any(CreateGymRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /gyms/{id} - Debería retornar 200 OK")
    void shouldGetGymById() throws Exception {
        GymResponse mockResponse = new GymResponse(1L, "Gym Test", "Address", "123", true);
        when(getGymByIdUseCase.execute(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/gyms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("GET /gyms - Debería retornar 200 OK y la lista de gyms")
    void shouldGetAllGyms() throws Exception {
        when(getAllGymsUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/gyms"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("PUT /gyms/{id} - Debería retornar 200 OK al actualizar")
    void shouldUpdateGym() throws Exception {
        UpdateGymRequest request = new UpdateGymRequest("Updated Name", "Address", "123");
        GymResponse mockResponse = new GymResponse(1L, "Updated Name", "Address", "123", true);

        when(updateGymUseCase.execute(eq(1L), any(UpdateGymRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/gyms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("DELETE /gyms/{id} - Debería retornar 204 No Content")
    void shouldDeleteGym() throws Exception {
        doNothing().when(deleteGymUseCase).execute(1L);

        mockMvc.perform(delete("/gyms/1"))
                .andExpect(status().isNoContent());
    }
}