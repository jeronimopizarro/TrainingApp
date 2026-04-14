package com.trainingapp.trainingapp.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.user.member.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // Casos de Uso
    @MockitoBean
    private RegisterMemberUseCase registerMemberUseCase;
    @MockitoBean
    private GetMemberByIdUseCase getMemberByIdUseCase;
    @MockitoBean
    private GetAllMembersByGymIdUseCase getAllMembersByGymIdUseCase;
    @MockitoBean
    private GetGymMembersSummaryUseCase getGymMembersSummaryUseCase;
    @MockitoBean
    private UpdateMemberUseCase updateMemberUseCase;
    @MockitoBean
    private DeleteMemberUseCase deleteMemberUseCase;

    // Dependencias de Seguridad
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /members - Debería retornar 201 Created al registrar un miembro válido")
    void shouldRegisterMemberAndReturn201() throws Exception {
        RegisterMemberRequest request = new RegisterMemberRequest(
                "Juan", "Perez", "juan@test.com", "pass123", "12345678", 10L,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );
        MemberResponse mockResponse = new MemberResponse(
                1L, "Juan", "Perez", "juan@test.com", "12345678", true, 10L,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        when(registerMemberUseCase.execute(any(RegisterMemberRequest.class))).thenReturn(
                mockResponse);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /members - Debería retornar 400 Bad Request si faltan campos obligatorios")
    void shouldReturn400WhenRegisterRequestIsInvalid() throws Exception {
        // Petición con nombre en blanco y email inválido para forzar el fallo en @Valid
        RegisterMemberRequest invalidRequest = new RegisterMemberRequest(
                "", "Perez", "email_invalido", "pass123", "12345678", 10L,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /members/{id} - Debería retornar 200 OK y los datos del miembro")
    void shouldGetMemberByIdAndReturn200() throws Exception {
        Long memberId = 1L;
        MemberResponse mockResponse = new MemberResponse(
                memberId, "Juan", "Perez", "juan@test.com", "12345678", true, 10L,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        when(getMemberByIdUseCase.execute(memberId)).thenReturn(mockResponse);

        mockMvc.perform(get("/members/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId))
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("GET /members?gymId={id} - Debería retornar 200 OK y la lista de miembros")
    void shouldGetAllMembersByGymIdAndReturn200() throws Exception {
        Long gymId = 10L;
        MemberResponse mockResponse = new MemberResponse(
                1L, "Juan", "Perez", "juan@test.com", "12345678", true, gymId,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        when(getAllMembersByGymIdUseCase.execute(gymId, null)).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/members")
                        .param("gymId", String.valueOf(gymId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].gymId").value(gymId));
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("GET /members?gymId={id}&status={status} - Debería retornar 200 OK y filtrar por estado")
    void shouldGetAllMembersByGymIdAndStatusAndReturn200() throws Exception {
        Long gymId = 10L;
        String status = "ACTIVE";
        MemberResponse mockResponse = new MemberResponse(
                1L, "Juan", "Perez", "juan@test.com", "12345678", true, gymId,
                LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        when(getAllMembersByGymIdUseCase.execute(gymId, status)).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/members")
                        .param("gymId", String.valueOf(gymId))
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("PUT /members/{id} - Debería retornar 200 OK al actualizar")
    void shouldUpdateMemberAndReturn200() throws Exception {
        Long memberId = 1L;
        UpdateMemberRequest request = new UpdateMemberRequest(
                "Carlos", "Gomez", "87654321", LocalDate.of(1991, 1, 1), "Definición"
        );
        MemberResponse mockResponse = new MemberResponse(
                memberId, "Carlos", "Gomez", "juan@test.com", "87654321", true, 10L,
                LocalDate.of(1991, 1, 1), "Definición"
        );

        when(updateMemberUseCase.execute(eq(memberId), any(UpdateMemberRequest.class))).thenReturn(
                mockResponse);

        mockMvc.perform(put("/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carlos"))
                .andExpect(jsonPath("$.primaryGoal").value("Definición"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("DELETE /members/{id} - Debería retornar 204 No Content al eliminar")
    void shouldDeleteMemberAndReturn204() throws Exception {
        Long memberId = 1L;

        doNothing().when(deleteMemberUseCase).execute(memberId);

        mockMvc.perform(delete("/members/{id}", memberId))
                .andExpect(status().isNoContent());
    }
}