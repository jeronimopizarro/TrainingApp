package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Debería cargar un usuario válido y transformarlo en UserDetails")
    void shouldLoadUserByUsernameSuccessfully() {
        String email = "test@test.com";
        UserJpaEntity mockEntity = mock(UserJpaEntity.class);

        when(mockEntity.getEmail()).thenReturn(email);
        when(mockEntity.getPassword()).thenReturn("encoded_password");
        when(mockEntity.getRole()).thenReturn(Role.MEMBER);

        when(userJpaRepository.findByEmailAndActiveTrue(email)).thenReturn(Optional.of(mockEntity));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encoded_password", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER")));
    }

    @Test
    @DisplayName("Debería lanzar UsernameNotFoundException si el usuario no existe o está inactivo")
    void shouldThrowExceptionWhenUserNotFound() {
        String email = "ghost@test.com";
        when(userJpaRepository.findByEmailAndActiveTrue(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}