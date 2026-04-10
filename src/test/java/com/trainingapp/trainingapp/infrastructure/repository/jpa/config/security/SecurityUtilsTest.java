package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.auth.UnauthenticatedUserException;
import com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        // Enlazamos nuestro Mock del contexto al Contexto Estático Global
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto para no afectar otros tests
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
    }

    @Test
    @DisplayName("Debería retornar el usuario actual si está autenticado")
    void shouldReturnCurrentUser() {
        String email = "juan@test.com";
        mockAuthentication(email);

        User mockUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        User result = securityUtils.getCurrentUser();

        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    @DisplayName("Debería lanzar error si el usuario no existe en BD a pesar del token")
    void shouldThrowExceptionIfUserNotExists() {
        String email = "ghost@test.com";
        mockAuthentication(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UnauthenticatedUserException.class, () -> securityUtils.getCurrentUser());
    }

    @Test
    @DisplayName("SuperAdmin siempre pasa validateSameGym")
    void superAdminPassesGymValidation() {
        String email = "super@test.com";
        mockAuthentication(email);

        User mockUser = mock(User.class);
        when(mockUser.isSuperAdmin()).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> securityUtils.validateSameGym(99L));
    }

    @Test
    @DisplayName("Debería pasar si un Member consulta info de su propio Gym")
    void memberPassesGymValidationIfSameGym() {
        String email = "member@test.com";
        mockAuthentication(email);

        Member mockMember = mock(Member.class);
        when(mockMember.isSuperAdmin()).thenReturn(false);
        when(mockMember.getGymId()).thenReturn(10L); // Su gym real
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> securityUtils.validateSameGym(10L));
    }

    @Test
    @DisplayName("Debería rechazar si un Admin intenta acceder a otro Gym")
    void adminRejectedIfAccessingDifferentGym() {
        String email = "admin@test.com";
        mockAuthentication(email);

        Admin mockAdmin = mock(Admin.class);
        when(mockAdmin.isSuperAdmin()).thenReturn(false);
        when(mockAdmin.getGymId()).thenReturn(10L); // Su gym real
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockAdmin));

        assertThrows(UnauthorizedGymAccessException.class, () -> securityUtils.validateSameGym(99L)); // Intenta acceder al gym 99
    }
}