package com.trainingapp.trainingapp.web.controller.dashboard;

import com.trainingapp.trainingapp.application.useCase.dashboard.AdminDashboardUseCase;
import com.trainingapp.trainingapp.application.useCase.dashboard.MemberDashboardUseCase;
import com.trainingapp.trainingapp.application.useCase.dashboard.TrainerDashboardUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(TestSecurityConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardUseCase adminDashboardUseCase;
    @MockitoBean
    private MemberDashboardUseCase memberDashboardUseCase;
    @MockitoBean
    private TrainerDashboardUseCase trainerDashboardUseCase;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Nested
    @DisplayName("Endpoints de Dashboards")
    class DashboardEndpoints {

        @Test
        @WithMockUser(roles = "GYM_ADMIN")
        @DisplayName("GET /dashboard/admin - Debería retornar 200 y métricas de Admin con estructura anidada")
        void shouldReturnAdminDashboard() throws Exception {
            // Arrange: Instanciamos usando los sub-records correspondientes
            AdminDashboardResponse.FinancialSummary financial = new AdminDashboardResponse.FinancialSummary(
                    new BigDecimal("5000.00"), // monthly
                    new BigDecimal("1200.00"), // weekly
                    new BigDecimal("200.00"),  // daily
                    new BigDecimal("4000.00"), // membership
                    new BigDecimal("1000.00")  // products
            );

            AdminDashboardResponse.AudienceSummary audience = new AdminDashboardResponse.AudienceSummary(
                    150L, // active
                    20L,  // new
                    5L    // churned
            );

            AdminDashboardResponse fakeResponse = new AdminDashboardResponse(
                    financial,
                    audience,
                    List.of(), // topProducts
                    List.of()  // expiringMemberships
            );

            when(adminDashboardUseCase.execute()).thenReturn(fakeResponse);

            // Act & Assert: Actualizamos los JsonPath para navegar la nueva jerarquía
            mockMvc.perform(get("/dashboard/admin"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.financialSummary.monthlyRevenue").value(5000.00))
                    .andExpect(jsonPath("$.audienceSummary.activeMembers").value(150))
                    .andExpect(jsonPath("$.audienceSummary.newMembersThisMonth").value(20));
        }

        @Test
        @WithMockUser(roles = "MEMBER")
        @DisplayName("GET /dashboard/member - Debería retornar 200 y métricas de Alumno")
        void shouldReturnMemberDashboard() throws Exception {
            // Arrange: Creamos la estructura de rutina activa y día sugerido
            MemberDashboardResponse.SuggestedDayDTO suggestedDay =
                    new MemberDashboardResponse.SuggestedDayDTO(1L, "Día de Pecho");

            MemberDashboardResponse.ActiveRoutineDTO activeRoutine =
                    new MemberDashboardResponse.ActiveRoutineDTO(
                            10L,
                            "Fuerza Base",
                            LocalDate.now().plusMonths(1),
                            suggestedDay
                    );

            MemberDashboardResponse fakeResponse = new MemberDashboardResponse(
                    15, // daysUntilExpiration
                    activeRoutine,
                    List.of(LocalDate.now().minusDays(1), LocalDate.now()) // trainingDaysThisMonth
            );

            when(memberDashboardUseCase.execute()).thenReturn(fakeResponse);

            // Act & Assert
            mockMvc.perform(get("/dashboard/member"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.daysUntilExpiration").value(15))
                    .andExpect(jsonPath("$.activeRoutine.name").value("Fuerza Base"))
                    .andExpect(jsonPath("$.activeRoutine.suggestedDay.name").value("Día de Pecho"))
                    // Verificamos que la lista de días de entrenamiento tenga 2 elementos
                    .andExpect(jsonPath("$.trainingDaysThisMonth.length()").value(2));
        }
    }
}