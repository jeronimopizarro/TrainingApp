/**
 ** Esta clase implementa la interfaz nativa UserDetailsService, para actuar como un "Traductor".
 * * Su única responsabilidad es recibir un email (username), buscar la entidad
 * correspondiente en nuestra base de datos (UserJpaEntity) y transformarla en un
 * objeto UserDetails estándar que el framework de Spring pueda entender para
 * verificar credenciales y autorizar roles.
 */
package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public CustomUserDetailsService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity userEntity = userJpaRepository.findByEmailAndActiveTrue(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

        return new User(userEntity.getEmail(), userEntity.getPassword(), Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())));
    }
}