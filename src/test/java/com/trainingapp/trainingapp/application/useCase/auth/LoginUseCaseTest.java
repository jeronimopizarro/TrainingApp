package com.trainingapp.trainingapp.application.useCase.auth;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.auth.AuthResponse;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private JwtService jwtService;

    @InjectMocks private LoginUseCase useCase;

    @Test
    @DisplayName("Debería retornar un token JWT válido si las credenciales son correctas")
    void shouldReturnJwtTokenOnSuccessfulLogin() {
        LoginRequest request = new LoginRequest("admin@test.com", "password123");
        UserDetails mockUserDetails = mock(UserDetails.class);
        String expectedToken = "jwt_token_super_seguro";

        // AuthenticationManager no lanza excepciones (autenticación exitosa)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(mockUserDetails);

        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn(expectedToken);

        AuthResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        // Verificamos que se intentó autenticar con los datos provistos
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Debería lanzar BadCredentialsException si las credenciales son inválidas")
    void shouldThrowExceptionOnBadCredentials() {
        LoginRequest request = new LoginRequest("admin@test.com", "wrong_password");

        // Simulamos que el AuthenticationManager lanza BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> useCase.execute(request));
    }
}