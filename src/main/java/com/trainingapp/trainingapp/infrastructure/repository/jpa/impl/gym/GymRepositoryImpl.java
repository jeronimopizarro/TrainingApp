package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym.GymMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.gym.GymJpaRespository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GymRepositoryImpl implements GymRepository {

    private final GymJpaRespository JpaRespository;
    private final GymMapper mapper;

    public GymRepositoryImpl(GymJpaRespository JpaRespository, GymMapper mapper) {
        this.JpaRespository = JpaRespository;
        this.mapper = mapper;
    }

    @Override
    public Gym save(Gym gym) {
        GymJpaEntity jpaEntity = mapper.toJpaEntity(gym);

        GymJpaEntity savedGym = JpaRespository.save(jpaEntity);

        return mapper.toDomain(savedGym);
    }

    @Override
    public Optional<Gym> findById(Long id) {
        return JpaRespository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Gym> findAll() {
        return JpaRespository.findAll().stream().map(mapper::toDomain).toList();
    }
}