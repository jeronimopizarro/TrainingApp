package com.trainingapp.trainingapp.application.usecase.auth;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.auth.AuthResponse;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public LoginUseCase(AuthenticationManager authenticationManager,
                        CustomUserDetailsService userDetailsService,
                        JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse execute(LoginRequest request) {
        // 1. Se verifica que el email y la contraseña coincidan.
        // Si la contraseña está mal, esto lanza una excepción automáticamente y corta el flujo.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Si pasó la validación, buscamos los datos del usuario para armar la pulsera.
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        // 3. Imprimimos el Token JWT.
        String jwtToken = jwtService.generateToken(userDetails);

        // 4. Se lo devolvemos al cliente.
        return new AuthResponse(jwtToken);
    }
}