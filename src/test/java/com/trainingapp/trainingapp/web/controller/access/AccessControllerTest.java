package com.trainingapp.trainingapp.web.controller.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.access.GenerateAccessQrUseCase;
import com.trainingapp.trainingapp.application.useCase.access.ValidateAccessUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.domain.enums.access.AccessMethod;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.access.QrTokenResponse;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessRequest;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class AccessControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GenerateAccessQrUseCase generateAccessQrUseCase;
    @MockitoBean private ValidateAccessUseCase validateAccessUseCase;

    // Seguridad
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /access/qr - Debería retornar 200 OK y el token QR")
    void shouldGenerateQrToken() throws Exception {
        Long memberId = 100L;
        QrTokenResponse mockResponse = new QrTokenResponse("token_seguro_123", 60);

        when(generateAccessQrUseCase.execute(memberId)).thenReturn(mockResponse);

        mockMvc.perform(get("/access/qr")
                        .param("memberId", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qrToken").value("token_seguro_123"))
                .andExpect(jsonPath("$.expiresInSeconds").value(60));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /access/validate - Debería retornar 200 OK al validar acceso con DNI")
    void shouldValidateAccess() throws Exception {
        // En el Request, el DNI es el "identifier" y usamos AccessMethod.DNI
        ValidateAccessRequest request = new ValidateAccessRequest("12345678", AccessMethod.DNI);

        ValidateAccessResponse mockResponse = new ValidateAccessResponse(
                true, "Juan Perez", "Acceso concedido"
        );

        when(validateAccessUseCase.execute(any(ValidateAccessRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/access/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessGranted").value(true))
                .andExpect(jsonPath("$.memberName").value("Juan Perez"))
                .andExpect(jsonPath("$.message").value("Acceso concedido"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /access/validate - Debería retornar 400 Bad Request si el DNI está en blanco")
    void shouldReturn400WhenIdentifierIsBlank() throws Exception {
        // DNI en blanco para forzar el fallo en el @NotBlank
        ValidateAccessRequest request = new ValidateAccessRequest("", AccessMethod.DNI);

        mockMvc.perform(post("/access/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /access/validate - Debería retornar 400 Bad Request si el método es nulo")
    void shouldReturn400WhenMethodIsNull() throws Exception {
        // Method nulo para forzar el fallo en el @NotNull
        ValidateAccessRequest request = new ValidateAccessRequest("12345678", null);

        mockMvc.perform(post("/access/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}