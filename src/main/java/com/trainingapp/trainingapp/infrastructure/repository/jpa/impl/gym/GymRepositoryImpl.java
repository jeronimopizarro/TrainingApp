package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym.GymMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.gym.GymJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GymRepositoryImpl implements GymRepository {

    private final GymJpaRepository JpaRepository;
    private final GymMapper mapper;

    public GymRepositoryImpl(GymJpaRepository JpaRepository, GymMapper mapper) {
        this.JpaRepository = JpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Gym save(Gym gym) {
        GymJpaEntity jpaEntity = mapper.toJpaEntity(gym);

        GymJpaEntity savedGym = JpaRepository.save(jpaEntity);

        return mapper.toDomain(savedGym);
    }

    @Override
    public Optional<Gym> findById(Long id) {
        return JpaRepository.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }

    @Override
    public List<Gym> findAll() {
        return JpaRepository.findAllByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }
}