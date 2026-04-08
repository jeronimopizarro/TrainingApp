package com.trainingapp.trainingapp.web.controller.routine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.routine.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.routine.RequestRoutineMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoutineController.class)
@Import(TestSecurityConfig.class) // Nuestra config limpia que hicimos en el Slice 1
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
        // Arrange: El DTO tiene availableDays en NULL, lo cual rompe tu validación @NotNull
        RequestRoutineMessage invalidRequest = new RequestRoutineMessage(
                null, null, ExperienceLevel.BEGINNER, "Ninguna", "Fuerza"
        );

        // Act & Assert
        mockMvc.perform(post("/routines/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(
                        status().isBadRequest()); // Verificamos que Spring corte la ejecución en la puerta
    }
}
