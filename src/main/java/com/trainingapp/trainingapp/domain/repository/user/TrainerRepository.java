package com.trainingapp.trainingapp.domain.repository.user;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository {

    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findByGymId(Long gymId);
}