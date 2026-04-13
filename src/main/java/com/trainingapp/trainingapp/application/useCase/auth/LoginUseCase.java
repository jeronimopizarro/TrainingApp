package com.trainingapp.trainingapp.application.useCase.auth;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.auth.AuthResponse;
import com.trainingapp.trainingapp.web.dto.auth.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Buscamos los datos extendidos del usuario para inyectar en el token.
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        
        // 3. Preparamos los "Claims" (datos extra) para el frontend.
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof CustomUserDetailsService.SecurityUser securityUser) {
            claims.put("role", securityUser.getRole());
            claims.put("gymId", securityUser.getGymId());
            claims.put("userName", securityUser.getFirstName());
        }

        // 4. Generamos el token enriquecido con el rol y el gimnasio.
        String jwtToken = jwtService.generateToken(claims, userDetails);

        // 5. Se lo devolvemos al cliente.
        return new AuthResponse(jwtToken);
    }
}