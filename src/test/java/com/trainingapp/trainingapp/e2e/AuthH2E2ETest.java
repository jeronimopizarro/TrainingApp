package com.trainingapp.trainingapp.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("e2e-h2")
// ESTA ES LA CLAVE: Fuerza a Spring a ignorar MySQL y levantar su propia H2 embebida
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class AuthH2E2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserJpaRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Limpiamos por las dudas si hubiera mugre en la H2 temporal
        userRepository.deleteAll();

        // Usamos un DNI súper random para evitar cualquier choque
        UserJpaEntity admin = new UserJpaEntity();
        admin.setFirstName("Admin"); admin.setLastName("H2");
        admin.setEmail("admin@h2.com"); admin.setPassword(passwordEncoder.encode("pass123"));
        admin.setDni("99887766"); admin.setRole(Role.SUPER_ADMIN); admin.setActive(true);
        userRepository.save(admin);
    }

    @Test
    @DisplayName("H2 E2E: Debería hacer login y devolver un JWT válido")
    void shouldLoginSuccessfullyInH2() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@h2.com", "pass123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}