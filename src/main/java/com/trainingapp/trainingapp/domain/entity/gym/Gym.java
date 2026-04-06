package com.trainingapp.trainingapp.domain.entity.gym;

import com.trainingapp.trainingapp.domain.exception.gym.GymAddressRequiredException;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyActiveException;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyInactiveException;
import com.trainingapp.trainingapp.domain.exception.gym.GymNameRequiredException;
import lombok.Getter;

@Getter
public class Gym {

    private final Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private boolean active;

    private Gym(Long id, String name, String address, String phoneNumber, boolean active) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.active = active;
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new GymNameRequiredException();
        }
        if (this.address == null || this.address.trim().isEmpty()) {
            throw new GymAddressRequiredException();
        }
    }

    public static Gym createNew(String name, String address, String phoneNumber) {
        return new Gym(null, name, address, phoneNumber, true);
    }

    public static Gym restore(Long id, String name, String address, String phoneNumber,
                              boolean active) {
        return new Gym(id, name, address, phoneNumber, active);
    }

    public void updateDetails(String newName, String newAddress, String phoneNumber) {
        this.name = newName;
        this.address = newAddress;
        this.phoneNumber = phoneNumber;
        validate();
    }

    public void deactivate() {
        if (!this.active) {
            throw new GymAlreadyInactiveException();
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new GymAlreadyActiveException();
        }
        this.active = true;
    }
}