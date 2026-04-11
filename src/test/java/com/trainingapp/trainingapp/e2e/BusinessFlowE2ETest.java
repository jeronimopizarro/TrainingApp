package com.trainingapp.trainingapp.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.gym.GymJpaRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.AdminJpaRepository;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class BusinessFlowE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AdminJpaRepository adminRepository;
    @Autowired private GymJpaRepository gymRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long gymId;

    @BeforeEach
    void setupDatabase() {
        // Crear Gimnasio
        GymJpaEntity gym = new GymJpaEntity();
        gym.setName("Gimnasio E2E");
        gym.setAddress("Calle Test 123");
        gym.setPhoneNumber("11223344");
        gym.setActive(true);
        gym = gymRepository.save(gym);
        this.gymId = gym.getId();

        // Crear Admin del Gimnasio
        AdminJpaEntity admin = new AdminJpaEntity();
        admin.setFirstName("Admin");
        admin.setLastName("Local");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setDni("11222333");
        admin.setRole(Role.GYM_ADMIN);
        admin.setActive(true);
        admin.setGymId(gymId);

        adminRepository.save(admin);
    }

    @Test
    @DisplayName("Flujo E2E: Ciclo completo de negocio (Plan -> Miembro -> Suscripción)")
    void shouldCompleteFullBusinessCycle() throws Exception {

        // LOGIN
        LoginRequest loginRequest = new LoginRequest("admin@test.com", "password123");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        // CREAR PLAN
        CreateMembershipPlanRequest planRequest = new CreateMembershipPlanRequest(
                "Plan Pro",
                "Acceso total",
                new BigDecimal("5000.00"),
                1,
                gymId
        );

        MvcResult planResult = mockMvc.perform(post("/membership-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long planId = ((Number) JsonPath.read(planResult.getResponse().getContentAsString(), "$.id")).longValue();

        // REGISTRAR MIEMBRO
        RegisterMemberRequest memberRequest = new RegisterMemberRequest(
                "Socio",
                "Nuevo",
                "socio@correo.com",
                "pass123",
                "99888777",
                gymId,
                LocalDate.of(1995, 5, 20),
                "Ganar masa muscular"
        );

        MvcResult memberResult = mockMvc.perform(post("/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long memberId = ((Number) JsonPath.read(memberResult.getResponse().getContentAsString(), "$.id")).longValue();

        // CREAR SUSCRIPCIÓN
        CreateSubscriptionRequest subRequest = new CreateSubscriptionRequest(
                memberId,
                planId,
                LocalDate.now(),
                PaymentMethod.CASH
        );

        mockMvc.perform(post("/subscriptions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}