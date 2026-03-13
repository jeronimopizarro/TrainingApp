package com.trainingapp.trainingapp.domain.entity.gym;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Gym {

    private Long  id;
    private String name;
    private String address;
    private String phone;
    private boolean active;

    public Gym(String name, String address, String phone) {
        validateName(name);
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.active = true;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Gym name cannot be null or empty.");
        }
    }

    public void updateDetails(String newName, String newAddress, String newPhone) {
        if (newName != null) {
            validateName(newName);
            this.name = newName;
        }
        if (newAddress != null) {
            this.address = newAddress;
        }
        if (newPhone != null) {
            this.phone = newPhone;
        }
    }

    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("El gimnasio ya se encuentra inactivo.");
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new IllegalStateException("El gimnasio ya se encuentra activo.");
        }
        this.active = true;
    }

    public void setId(Long id) {
        this.id = id;
    }
}