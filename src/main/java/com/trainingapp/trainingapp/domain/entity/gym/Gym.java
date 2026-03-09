package com.trainingapp.trainingapp.domain.entity.gym;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Gym {
    private Long  id;
    private String name;
    private String address;
    private String phone;
    private boolean active;

    public Gym(String name, String adress, String phone) {
        validateName(name);
        this.name = name;
        this.address = adress;
        this.phone = phone;
        this.active = true;
    }

    public void updateDetails(String newName, String newAdress, String newPhone) {
        validateName(newName);
        this.name = newName;
        this.address = newAdress;
        this.phone = newPhone;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Gym name cannot be null or empty.");
        }
    }

    public void desactive(){
        this.active = false;
    }
    public void active(){
        this.active = true;
    }
}