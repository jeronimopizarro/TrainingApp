package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.ReceptionistJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceptionistJpaRepository extends JpaRepository<ReceptionistJpaEntity, Long> {
    Optional<ReceptionistJpaEntity> findByIdAndActiveTrue(Long id);

    List<ReceptionistJpaEntity> findAllByGymIdAndActiveTrue(Long gymId);
}