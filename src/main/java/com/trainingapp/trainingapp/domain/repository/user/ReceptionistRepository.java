package com.trainingapp.trainingapp.domain.repository.user;

import com.trainingapp.trainingapp.domain.entity.user.Receptionist;

import java.util.List;
import java.util.Optional;

public interface ReceptionistRepository {
    Receptionist save(Receptionist receptionist);
    Optional<Receptionist> findById(Long id);
    List<Receptionist> findAllByGymId(Long gymId);
}