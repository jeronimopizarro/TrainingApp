package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.ReceptionistJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.ReceptionistMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.ReceptionistJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReceptionistRepositoryImpl implements ReceptionistRepository {

    private final ReceptionistJpaRepository jpaRepository;
    private final ReceptionistMapper mapper;

    public ReceptionistRepositoryImpl(ReceptionistJpaRepository jpaRepository, ReceptionistMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Receptionist save(Receptionist receptionist) {
        ReceptionistJpaEntity entity = mapper.toEntity(receptionist);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Receptionist> findById(Long id) {
        return jpaRepository.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }

    @Override
    public List<Receptionist> findAllByGymId(Long gymId) {
        return jpaRepository.findAllByGymIdAndActiveTrue(gymId).stream().map(mapper::toDomain).toList();
    }
}