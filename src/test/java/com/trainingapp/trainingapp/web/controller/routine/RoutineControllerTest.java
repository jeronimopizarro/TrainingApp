package com.trainingapp.trainingapp.web.controller.routine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.routine.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.routine.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDate;


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
        @WithMockUser(roles = "MEMBER")
        @DisplayName("POST /routines/personal - Debería retornar 201 al crear rutina personal")
        void shouldReturn201WhenCreatingPersonalRoutine() throws Exception {
            // Armamos un DTO válido con 1 día y 1 ejercicio adentro para pasar el @Valid
            CreatePersonalRoutineRequest.CreateRoutineDetailRequest exercise =
                    new CreatePersonalRoutineRequest.CreateRoutineDetailRequest(1L, 4, 8, 12, 2,
                            50.0, "Nota");
            CreatePersonalRoutineRequest.CreateTrainingDayRequest day =
                    new CreatePersonalRoutineRequest.CreateTrainingDayRequest("Día 1",
                            List.of(exercise));

            CreatePersonalRoutineRequest request =
                    new CreatePersonalRoutineRequest("Rutina Fuerza", List.of(day));

            CreateRoutineResponse fakeResponse =
                    new CreateRoutineResponse(50L, "Rutina personal creada con éxito.");

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
                    new AssignRoutineRequest.CreateRoutineDetailRequest(1L, 4, 8, 12, 2, 50.0,
                            "Nota");
            AssignRoutineRequest.CreateTrainingDayRequest day =
                    new AssignRoutineRequest.CreateTrainingDayRequest("Día 1", List.of(exercise));

            AssignRoutineRequest request =
                    new AssignRoutineRequest("Rutina Base Adaptada", 100L, List.of(day));
            CreateRoutineResponse fakeResponse =
                    new CreateRoutineResponse(51L, "Rutina Base Adaptada");

            when(assignRoutineUseCase.execute(any(AssignRoutineRequest.class)))
                    .thenReturn(fakeResponse);

            mockMvc.perform(post("/routines/assign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated()) // Esperamos un 201 Created
                    .andExpect(jsonPath("$.id").value(51L));
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName(
                "POST /routines/requests/{id}/take - Debería retornar 200 al tomar una solicitud")
        void shouldReturn200WhenTakingRequest() throws Exception {
            doNothing().when(takeRoutineRequestUseCase).execute(1L);

            mockMvc.perform(post("/routines/requests/1/take"))
                    .andExpect(status().isOk()); // Esperamos un HTTP 200 OK
        }
    }

    @Nested
    @DisplayName("Endpoints de Consultas (Queries)")
    class QueryEndpoints {

        @Test
        @WithMockUser(roles = "MEMBER")
        @DisplayName("GET /routines/{id} - Debería retornar 200 y el detalle de la rutina")
        void shouldGetRoutineById() throws Exception {
            RoutineDetailResponse mockResponse = Mockito.mock(RoutineDetailResponse.class);

            when(getRoutineByIdUseCase.execute(1L)).thenReturn(mockResponse);

            mockMvc.perform(get("/routines/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "MEMBER")
        @DisplayName(
                "GET /routines/member/{memberId}/active - Debería retornar 200 y la rutina activa")
        void shouldGetActiveRoutine() throws Exception {
            RoutineResponse mockResponse = Mockito.mock(RoutineResponse.class);

            when(getActiveRoutineUseCase.execute(100L)).thenReturn(mockResponse);

            mockMvc.perform(get("/routines/active").param("memberId", "100"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GYM_ADMIN")
        @DisplayName(
                "GET /routines/member/{memberId} - Debería retornar 200 y la lista de rutinas del alumno")
        void shouldGetAllRoutinesByMember() throws Exception {
            when(getAllRoutinesByMemberIdUseCase.execute(100L)).thenReturn(List.of());

            mockMvc.perform(get("/routines").param("memberId", "100"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GYM_ADMIN")
        @DisplayName(
                "GET /routines/trainer/{trainerId} - Debería retornar 200 y la lista de rutinas del profe")
        void shouldGetAllRoutinesByTrainer() throws Exception {
            when(getAllRoutinesByTrainerIdUseCase.execute(2L)).thenReturn(List.of());

            mockMvc.perform(get("/routines").param("trainerId", "2"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Endpoints de Gestión de Estados")
    class StateManagementEndpoints {

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName("PATCH /routines/{id}/activate - Debería retornar 200 al activar")
        void shouldActivateRoutine() throws Exception {
            ActivateRoutineRequest request =
                    new ActivateRoutineRequest(2L, LocalDate.now(), LocalDate.now().plusMonths(1));

            RoutineResponse mockResponse = Mockito.mock(RoutineResponse.class);

            when(activateRoutineUseCase.execute(eq(1L), any())).thenReturn(mockResponse);

            mockMvc.perform(patch("/routines/1/activate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName("PATCH /routines/{id}/inactive - Debería retornar 200 al inactivar")
        void shouldInactiveRoutine() throws Exception {
            RoutineResponse mockResponse =
                    Mockito.mock(RoutineResponse.class);

            when(inactiveRoutineUseCase.execute(1L)).thenReturn(mockResponse);

            mockMvc.perform(patch("/routines/1/inactive")).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "MEMBER")
        @DisplayName("PATCH /routines/{id}/complete - Debería retornar 200 al completar")
        void shouldCompleteRoutine() throws Exception {
            doNothing().when(completeRoutineUseCase).execute(1L);

            mockMvc.perform(patch("/routines/1/complete")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Endpoints de Modificación (Update, Duplicate, Delete)")
    class ModificationEndpoints {

        @Test
        @WithMockUser(roles = "MEMBER")
        @DisplayName("POST /routines/request - Debería retornar 200 al pedir rutina exitosamente")
        void shouldRequestRoutineSuccessfully() throws Exception {
            RequestRoutineMessage request = new RequestRoutineMessage(
                    null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Fuerza"
            );
            doNothing().when(requestRoutineUseCase).execute(any(RequestRoutineMessage.class));

            mockMvc.perform(post("/routines/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName("PUT /routines/{id} - Debería retornar 200 al actualizar rutina")
        void shouldUpdateRoutine() throws Exception {
            UpdateRoutineRequest.UpdateRoutineDetailRequest mockExercise =
                    new UpdateRoutineRequest.UpdateRoutineDetailRequest(
                            1L, 1L, 4, 8, 12, 2, 50.0, "Nota"
                    );

            UpdateRoutineRequest.UpdateTrainingDayRequest mockDay =
                    new UpdateRoutineRequest.UpdateTrainingDayRequest(null, "Lunes", List.of(mockExercise));

           UpdateRoutineRequest request =
                    new UpdateRoutineRequest("Modificada", 2L, List.of(mockDay));

            CreateRoutineResponse mockResponse = new CreateRoutineResponse(1L, "Routine updated successfully");

            when(updateRoutineUseCase.execute(eq(1L), any())).thenReturn(mockResponse);

            mockMvc.perform(put("/routines/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName("POST /routines/{id}/duplicate - Debería retornar 201 al duplicar")
        void shouldDuplicateRoutine() throws Exception {
            DuplicateRoutineRequest request = new DuplicateRoutineRequest("Copia", 100L, 2L, 2L);

            CreateRoutineResponse mockResponse =
                    new CreateRoutineResponse(2L, "Routine duplicated successfully");

            when(duplicateRoutineUseCase.execute(eq(1L), any())).thenReturn(mockResponse);

            mockMvc.perform(post("/routines/1/duplicate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        @DisplayName("DELETE /routines/{id} - Debería retornar 200/204 al eliminar")
        void shouldDeleteRoutine() throws Exception {
            doNothing().when(deleteRoutineUseCase).execute(1L);

            mockMvc.perform(delete("/routines/1"))
                    .andExpect(status().isNoContent());
        }
    }
}
