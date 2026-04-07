package com.trainingapp.trainingapp.web.controller.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.tracker.FinishTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.LogTrainingSetUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.StartTrainingSessionUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.tracker.LogSetRequest;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.StartSessionRequest;
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
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingSessionController.class)
@Import(TestSecurityConfig.class) // CARGAMOS LA CONFIG DE SEGURIDAD PROFESIONAL
@AutoConfigureJson
class TrainingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private StartTrainingSessionUseCase startSessionUseCase;

    @MockitoBean
    private LogTrainingSetUseCase logSetUseCase;

    @MockitoBean
    private FinishTrainingSessionUseCase finishSessionUseCase;

    @Test
    @WithMockUser(roles = "MEMBER") // Simulamos de forma limpia un usuario con rol
    @DisplayName("Debería retornar 201 CREATED al iniciar sesión correctamente")
    void shouldStartSessionSuccessfully() throws Exception {
        StartSessionRequest request = new StartSessionRequest(100L, 5L);
        SessionResponse fakeResponse = new SessionResponse(
                1L, 10L, 100L, LocalDateTime.now(), null, SessionStatus.IN_PROGRESS
        );

        when(startSessionUseCase.execute(any(StartSessionRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/sessions/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Verificamos el 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("Debería retornar 400 BAD REQUEST si falta el exerciseId")
    void shouldReturn400WhenExerciseIsMissing() throws Exception {
        LogSetRequest invalidRequest = new LogSetRequest(
                null, 1, 10, BigDecimal.valueOf(50), 2, "Nota"
        );

        mockMvc.perform(post("/sessions/1/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}