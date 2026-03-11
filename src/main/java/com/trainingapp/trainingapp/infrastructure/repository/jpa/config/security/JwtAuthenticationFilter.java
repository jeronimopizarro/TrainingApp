/**
 * * Este filtro actúa como la primera línea de defensa de la API. Se ejecuta una vez
 * por cada petición HTTP.
 * 1. Extraer el token JWT del encabezado 'Authorization' (esquema Bearer).
 * 2. Delegar la extracción del usuario (email) y la validación de la firma
 * criptográfica al JwtService.
 * 3. Si el token es auténtico y no está vencido, recupera los roles del usuario
 * desde la base de datos y lo registra como "Autenticado" en el SecurityContextHolder.
 * 4. Permitir que la petición continúe su viaje hacia el Controlador correspondiente.
 */
package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extraemos el encabezado 'Authorization' que contiene el token JWT de la petición HTTP.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Verificamos la existencia y el formato del token.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Aislamos el token puro.
        jwt = authHeader.substring(7);

        // Extraemos el 'subject' (email del usuario) desde el payload del token.
        userEmail = jwtService.extractUsername(jwt);

        // Validamos que el email exista en el token y que el usuario no esté previamente autenticado en el contexto de seguridad actual.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Recuperamos los detalles del usuario desde la base de datos.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //Validamos el token (firma y expiración) contra los datos del usuario.
            if (jwtService.isTokenValid(jwt, userDetails)) {

                //Si el token es válido, instanciamos el objeto de autenticación inyectando los privilegios correspondientes.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}