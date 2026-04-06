package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.*;
import lombok.Getter;

@Getter
public abstract class User {

    private final Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dni;
    private Role role;
    private boolean active;


    protected User(Long id, String firstName, String lastName, String email, String password, String dni, Role role, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dni = dni;
        this.role = role;
        this.active = active;
        validateBaseUser();
    }

    private void validateBaseUser() {
        if (this.email == null || this.email.trim().isEmpty() || !this.email.contains("@")) {
            throw new InvalidEmailException();
        }
        if (this.firstName == null || this.firstName.trim().isEmpty()) {
            throw new UserFirstNameRequiredException();
        }
        if (this.lastName == null || this.lastName.trim().isEmpty()) {
            throw new UserLastNameRequiredException();
        }
        if (this.password == null || this.password.trim().isEmpty()) {
            throw new UserPasswordRequiredException();
        }
        if (this.dni == null || this.dni.trim().isEmpty()) {
            throw new UserDniRequiredException();
        }
    }

    public void updateBaseDetails(String firstName, String lastName, String dni) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        validateBaseUser();
    }

    public void deactivate() {
        if (!this.active) throw new UserAlreadyInactiveException();
        this.active = false;
    }

    public void activate() {
        if (this.active) throw new UserAlreadyActiveException();
        this.active = true;
    }

    public boolean isSuperAdmin() {
        return this.role == Role.SUPER_ADMIN;
    }

    public boolean isGymAdmin() {
        return this.role == Role.GYM_ADMIN;
    }

    public boolean isTrainer() {
        return this.role == Role.TRAINER;
    }

    public boolean isMember() {
        return this.role == Role.MEMBER;
    }
}