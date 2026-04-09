package com.trainingapp.trainingapp.web.controller.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.exercise.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import com.trainingapp.trainingapp.web.dto.exercise.UpdateExerciseRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExerciseController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class ExerciseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateExerciseUseCase createExerciseUseCase;
    @MockitoBean private GetExerciseByIdUseCase getExerciseByIdUseCase;
    @MockitoBean private GetExercisesUseCase getExercisesUseCase;
    @MockitoBean private UpdateExerciseUseCase updateExerciseUseCase;
    @MockitoBean private DeleteExerciseUseCase deleteExerciseUseCase;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("POST /exercises - Debería retornar 201 Created")
    void shouldCreateExercise() throws Exception {
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Sentadilla", "Desc", "img", "vid", false,
                List.of(new CreateExerciseRequest.MuscleGroupAssignmentRequest(1L, true))
        );

        ExerciseResponse mockResponse = new ExerciseResponse(1L, "Ejercicio creado exitosamente");

        when(createExerciseUseCase.execute(any(CreateExerciseRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Ejercicio creado exitosamente"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /exercises/{id} - Debería retornar 200 OK")
    void shouldGetExerciseById() throws Exception {
        ExerciseDetailResponse mockResponse = mock(ExerciseDetailResponse.class);
        when(getExerciseByIdUseCase.execute(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/exercises/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /exercises?muscleGroupId=1 - Debería retornar 200 OK y la lista de ejercicios")
    void shouldGetAllExercises() throws Exception {
        when(getExercisesUseCase.execute(1L)).thenReturn(List.of());

        mockMvc.perform(get("/exercises").param("muscleGroupId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("PUT /exercises/{id} - Debería retornar 200 OK al actualizar")
    void shouldUpdateExercise() throws Exception {
        // Agregamos un elemento a la lista para pasar la validación @NotEmpty del DTO
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "Sentadilla", "Desc", "img", "vid", false,
                List.of(new UpdateExerciseRequest.MuscleGroupAssignmentRequest(1L, true))
        );

        ExerciseResponse mockResponse = new ExerciseResponse(1L, "Ejercicio actualizado exitosamente");

        when(updateExerciseUseCase.execute(eq(1L), any(UpdateExerciseRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Ejercicio actualizado exitosamente"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("DELETE /exercises/{id} - Debería retornar 204 No Content")
    void shouldDeleteExercise() throws Exception {
        doNothing().when(deleteExerciseUseCase).execute(1L);

        mockMvc.perform(delete("/exercises/1"))
                .andExpect(status().isNoContent());
    }
}