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

    private final GymJpaRepository jpaRepository;
    private final GymMapper mapper;

    public GymRepositoryImpl(GymJpaRepository JpaRepository, GymMapper mapper) {
        this.jpaRepository = JpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Gym save(Gym gym) {
        GymJpaEntity jpaEntity = mapper.toJpaEntity(gym);

        GymJpaEntity savedGym = jpaRepository.save(jpaEntity);

        return mapper.toDomain(savedGym);
    }

    @Override
    public Optional<Gym> findById(Long id) {
        return jpaRepository.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }

    @Override
    public List<Gym> findAll() {
        return jpaRepository.findAllByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameAndActiveTrue(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return jpaRepository.existsByNameAndIdNotAndActiveTrue(name, id);
    }
}