package com.trainingapp.trainingapp.web.controller.tracker;

import com.trainingapp.trainingapp.application.useCase.tracker.GetExerciseProgressUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.GetMemberProgressSummaryUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.tracker.ExerciseProgressResponse;
import com.trainingapp.trainingapp.web.dto.tracker.MemberProgressSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberProgressController.class)
@Import(TestSecurityConfig.class)
class MemberProgressControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetMemberProgressSummaryUseCase getMemberProgressSummaryUseCase;
    @MockitoBean private GetExerciseProgressUseCase getExerciseProgressUseCase;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /tracker/progress/summary - Debería retornar 200 y el resumen")
    void shouldReturnSummary() throws Exception {
        MemberProgressSummaryResponse.ExerciseSummaryDTO dto =
                new MemberProgressSummaryResponse.ExerciseSummaryDTO(1L, "Press Banca", new BigDecimal("100.5"), "http://image.com");

        MemberProgressSummaryResponse mockResponse = new MemberProgressSummaryResponse(List.of(dto));

        when(getMemberProgressSummaryUseCase.execute(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/progress/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises[0].exerciseName").value("Press Banca"))
                .andExpect(jsonPath("$.exercises[0].currentPersonalRecord").value(100.5));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /tracker/progress/exercise/{exerciseId} - Debería retornar 200")
    void shouldReturnExerciseProgress() throws Exception {
        ExerciseProgressResponse mockResponse = new ExerciseProgressResponse(5L, "Sentadilla", List.of());

        when(getExerciseProgressUseCase.execute(eq(5L), any(), anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/progress/exercise/5").param("monthsBack", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value("Sentadilla"));
    }
}