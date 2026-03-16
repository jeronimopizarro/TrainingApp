package com.trainingapp.trainingapp.domain.repository.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import java.util.List;
import java.util.Optional;

public interface GymRepository {
    Gym save(Gym gym);
    Optional<Gym> findById(Long id);
    List<Gym> findAll();
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsById(Long id);
}