/**
 ** Esta clase implementa la interfaz nativa UserDetailsService, para actuar como un "Traductor".
 * * Su única responsabilidad es recibir un email (username), buscar la entidad
 * correspondiente en nuestra base de datos (UserJpaEntity) y transformarla en un
 * objeto UserDetails estándar que el framework de Spring pueda entender para
 * verificar credenciales y autorizar roles.
 */
package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.ReceptionistJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.UserJpaRepository;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

        Long gymId = null;
        if (userEntity instanceof AdminJpaEntity) {
            gymId = ((AdminJpaEntity) userEntity).getGymId();
        } else if (userEntity instanceof TrainerJpaEntity) {
            gymId = ((TrainerJpaEntity) userEntity).getGymId();
        } else if (userEntity instanceof ReceptionistJpaEntity) {
            gymId = ((ReceptionistJpaEntity) userEntity).getGymId();
        } else if (userEntity instanceof MemberJpaEntity) {
            gymId = ((MemberJpaEntity) userEntity).getGymId();
        }

        return new SecurityUser(
                userEntity.getEmail(),
                userEntity.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())),
                userEntity.getRole().name(),
                gymId,
                userEntity.getFirstName()
        );
    }

    /**
     * Clase personalizada para extender los detalles del usuario de Spring Security.
     * Esto nos permite transportar datos extra (como gymId y firstName) a través del flujo de autenticación.
     */
    @Getter
    public static class SecurityUser extends org.springframework.security.core.userdetails.User {
        private final String role;
        private final Long gymId;
        private final String firstName;

        public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                            String role, Long gymId, String firstName) {
            super(username, password, authorities);
            this.role = role;
            this.gymId = gymId;
            this.firstName = firstName;
        }
    }
}