package com.trainingapp.trainingapp.web.controller.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.tracker.CancelTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.FinishTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.GetActiveTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.LogTrainingSetUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.StartTrainingSessionUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.tracker.SessionStatus;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.tracker.LogSetRequest;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.SetLogResponse;
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
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingSessionController.class)
@Import(TestSecurityConfig.class)
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
    private StartTrainingSessionUseCase startTrainingSessionUseCase;

    @MockitoBean
    private LogTrainingSetUseCase logTrainingSetUseCase;

    @MockitoBean
    private FinishTrainingSessionUseCase finishTrainignSessionUseCase;

    @MockitoBean
    private GetActiveTrainingSessionUseCase getActiveTrainingSessionUseCase;

    @MockitoBean
    private CancelTrainingSessionUseCase cancelSessionUseCase;

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("Debería retornar 201 CREATED al iniciar sesión correctamente")
    void shouldStartSessionSuccessfully() throws Exception {
        StartSessionRequest request = new StartSessionRequest(100L, 5L);
        SessionResponse fakeResponse = new SessionResponse(
                1L, 10L, 100L, 5L, LocalDateTime.now(), null, SessionStatus.IN_PROGRESS, new ArrayList<>()
        );

        when(startTrainingSessionUseCase.execute(any(StartSessionRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/sessions/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
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

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("POST /sessions/{sessionId}/sets - Debería retornar 201 al registrar serie")
    void shouldLogSet() throws Exception {
        LogSetRequest request = new LogSetRequest(
                5L, 1, 10, new BigDecimal("50.0"), 2, "Nota"
        );

        SetLogResponse mockResponse = new SetLogResponse(
                1L, 5L, 1, 10, new BigDecimal("50.0"), 2, "Nota"
        );

        when(logTrainingSetUseCase.execute(eq(1L), any(LogSetRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/sessions/1/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.weightLifted").value(50.0));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("PATCH /sessions/{sessionId}/finish - Debería retornar 200 al finalizar")
    void shouldFinishSession() throws Exception {
        SessionResponse mockResponse = new SessionResponse(
                1L, 10L, 100L, 5L, LocalDateTime.now(), LocalDateTime.now(), SessionStatus.COMPLETED, new ArrayList<>()
        );

        when(finishTrainignSessionUseCase.execute(1L)).thenReturn(mockResponse);

        mockMvc.perform(patch("/sessions/1/finish"))
                .andExpect(status().isOk());
    }
}
