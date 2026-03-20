package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

@Getter
public abstract class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dni;
    private Role role;
    private boolean active;


    protected User(String firstName, String lastName, String email, String password, String dni,Role role) {
        validateBasicData(firstName, lastName, email, password, dni);

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dni = dni;
        this.role = role;
        this.active = true;
    }

    private void validateBasicData(String firstName, String lastName, String email,
                                   String password, String dni) {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be empty.");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name cannot be empty.");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email format.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (dni == null || dni.isBlank())
            throw new IllegalArgumentException("DNI cannot be empty.");
    }

    protected void updateBasicProfile(String firstName, String lastName) {
        if (firstName != null) {
            if (firstName.isBlank()) throw new IllegalArgumentException("First name cannot be empty.");
            this.firstName = firstName;
        }
        if (lastName != null) {
            if (lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be empty.");
            this.lastName = lastName;
        }
    }

    public void deactivate() {
        this.active = false;
        //Evitamos errores con el UNIQUE de la BDD.
        this.email = this.email + "_deleted_" + System.currentTimeMillis();
    }

    public void activate() {
        this.active = true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
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