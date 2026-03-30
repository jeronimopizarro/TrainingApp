package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String dni;

    @Enumerated(EnumType.STRING) // Guarda el texto 'SUPER_ADMIN', 'MEMBER', etc.
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false)
    private boolean active;
}
