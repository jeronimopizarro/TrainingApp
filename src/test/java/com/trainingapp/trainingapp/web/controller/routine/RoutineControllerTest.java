package com.trainingapp.trainingapp.web.controller.routine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.routine.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.routine.AssignRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreatePersonalRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.RequestRoutineMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoutineController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class RoutineControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestRoutineUseCase requestRoutineUseCase;

    @MockitoBean
    private AssignRoutineUseCase assignRoutineUseCase;
    @MockitoBean
    private CreatePersonalRoutineUseCase createPersonalRoutineUseCase;
    @MockitoBean
    private GetRoutineByIdUseCase getRoutineByIdUseCase;
    @MockitoBean
    private GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase;
    @MockitoBean
    private ActivateRoutineUseCase activateRoutineUseCase;
    @MockitoBean
    private GetActiveRoutineUseCase getActiveRoutineUseCase;
    @MockitoBean
    private InactiveRoutineUseCase inactiveRoutineUseCase;
    @MockitoBean
    private DuplicateRoutineUseCase duplicateRoutineUseCase;
    @MockitoBean
    private UpdateRoutineUseCase updateRoutineUseCase;
    @MockitoBean
    private DeleteRoutineUseCase deleteRoutineUseCase;
    @MockitoBean
    private CompleteRoutineUseCase completeRoutineUseCase;
    @MockitoBean
    private GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase;

    @MockitoBean
    private TakeRoutineRequestUseCase takeRoutineRequestUseCase;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("Debería retornar 400 Bad Request si no se especifican los días disponibles")
    void requestRoutine_WhenAvailableDaysIsNull_ShouldReturn400() throws Exception {
        RequestRoutineMessage invalidRequest = new RequestRoutineMessage(
                null, null, ExperienceLevel.BEGINNER, "Ninguna", "Fuerza"
        );

        // Act & Assert
        mockMvc.perform(post("/routines/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(
                        status().isBadRequest());
    }

    @Nested
    @DisplayName("Endpoints de Asignación y Creación de Rutinas")
    class AssignmentEndpoints {

        @Test
        @WithMockUser(roles = "MEMBER") // Este endpoint es del Alumno (Member)
        @DisplayName("POST /routines/personal - Debería retornar 201 al crear rutina personal")
        void shouldReturn201WhenCreatingPersonalRoutine() throws Exception {
            // Armamos un DTO válido con 1 día y 1 ejercicio adentro para pasar el @Valid
            CreatePersonalRoutineRequest.CreateRoutineDetailRequest exercise =
                    new CreatePersonalRoutineRequest.CreateRoutineDetailRequest(1L, 4, 8, 12, 2, 50.0, "Nota");
            CreatePersonalRoutineRequest.CreateTrainingDayRequest day =
                    new CreatePersonalRoutineRequest.CreateTrainingDayRequest("Día 1", List.of(exercise));

            CreatePersonalRoutineRequest request = new CreatePersonalRoutineRequest("Rutina Fuerza", List.of(day));

            CreateRoutineResponse fakeResponse = new CreateRoutineResponse(50L, "Rutina personal creada con éxito.");

            when(createPersonalRoutineUseCase.execute(any(CreatePersonalRoutineRequest.class)))
                    .thenReturn(fakeResponse);

            mockMvc.perform(post("/routines/personal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(50L))
                    .andExpect(jsonPath("$.message").value("Rutina personal creada con éxito."));
        }

        @Test
        @WithMockUser(roles = "TRAINER") // Este endpoint sí es del Profe (Trainer)
        @DisplayName("POST /routines/assign - Debería retornar 201 al asignar una rutina base")
        void shouldReturn201WhenAssigningRoutine() throws Exception {
            // Armamos un DTO válido para pasar el @Valid
            AssignRoutineRequest.CreateRoutineDetailRequest exercise =
                    new AssignRoutineRequest.CreateRoutineDetailRequest(1L, 4, 8, 12, 2, 50.0, "Nota");
            AssignRoutineRequest.CreateTrainingDayRequest day =
                    new AssignRoutineRequest.CreateTrainingDayRequest("Día 1", List.of(exercise));

            AssignRoutineRequest request = new AssignRoutineRequest("Rutina Base Adaptada", 100L, List.of(day));
            CreateRoutineResponse fakeResponse = new CreateRoutineResponse(51L, "Rutina Base Adaptada");

            when(assignRoutineUseCase.execute(any(AssignRoutineRequest.class)))
                    .thenReturn(fakeResponse);

            mockMvc.perform(post("/routines/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated()) // Esperamos un 201 Created
                    .andExpect(jsonPath("$.id").value(51L));
        }

        @Test
        @WithMockUser(roles = "TRAINER") // Solo los profes (o admins) pueden tomar solicitudes
        @DisplayName("POST /routines/requests/{id}/take - Debería retornar 200 al tomar una solicitud")
        void shouldReturn200WhenTakingRequest() throws Exception {
            // Como el caso de uso devuelve 'void', usamos doNothing()
            doNothing().when(takeRoutineRequestUseCase).execute(1L);

            mockMvc.perform(post("/routines/requests/1/take"))
                    .andExpect(status().isOk()); // Esperamos un HTTP 200 OK
        }
    }
}
