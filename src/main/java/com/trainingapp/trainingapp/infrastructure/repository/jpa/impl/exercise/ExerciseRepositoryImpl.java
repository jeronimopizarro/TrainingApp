package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.ExerciseMuscleGroupJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.exercise.MuscleGroupJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise.ExerciseMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise.ExerciseJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final ExerciseJpaRepository jpaRepository;
    private final ExerciseMapper mapper;
    private final com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise.MuscleGroupJpaRepository muscleGroupJpaRepository;

    public ExerciseRepositoryImpl(ExerciseJpaRepository jpaRepository, ExerciseMapper mapper, com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise.MuscleGroupJpaRepository muscleGroupJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.muscleGroupJpaRepository = muscleGroupJpaRepository;
    }

    @Override
    @Transactional
    public Exercise save(Exercise domain) {
        ExerciseJpaEntity entityToSave;

        if (domain.getId() != null) {
            entityToSave = jpaRepository.findById(domain.getId())
                    .orElse(new ExerciseJpaEntity());
        } else {
            entityToSave = mapper.toEntity(domain);
            // Limpiamos los muscle groups temporales del mapper para reconstruirlos con entidades reales
            entityToSave.getMuscleGroups().clear();
        }

        // Centralizamos la actualización de datos y la asociación de entidades reales
        updateEntityFromDomain(entityToSave, domain);
        
        ExerciseJpaEntity savedEntity = jpaRepository.saveAndFlush(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    private void updateEntityFromDomain(ExerciseJpaEntity entity, Exercise domain) {
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setImageUrl(domain.getImageUrl());
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setIsBase(domain.getIsBase());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setGymId(domain.getGymId());
        entity.setActive(domain.isActive());

        entity.getMuscleGroups().clear();

        if (domain.getMuscleGroups() != null) {
            domain.getMuscleGroups().forEach(mgDomain -> {
                ExerciseMuscleGroupJpaEntity mgEntity = new ExerciseMuscleGroupJpaEntity();

                // BUSCAMOS EL MÚSCULO REAL EN LA DB
                MuscleGroupJpaEntity muscle = muscleGroupJpaRepository.findById(mgDomain.getMuscleGroupId())
                        .orElseThrow(() -> new RuntimeException("Muscle group not found: " + mgDomain.getMuscleGroupId()));

                mgEntity.setMuscleGroup(muscle);
                mgEntity.setExercise(entity);
                mgEntity.setPrimary(mgDomain.isPrimary());

                // Seteamos manualmente el ID compuesto
                if (entity.getId() != null) {
                    mgEntity.getId().setExerciseId(entity.getId());
                }
                mgEntity.getId().setMuscleGroupId(mgDomain.getMuscleGroupId());

                entity.getMuscleGroups().add(mgEntity);
            });
        }
    }

    @Override
    public Optional<Exercise> findById(Long id) {
        Optional<ExerciseJpaEntity> entity = jpaRepository.findById(id);
        return entity.map(mapper::toDomain);
    }

    @Override
    public List<Exercise> findAll() {
        List<ExerciseJpaEntity> exerciseFound = jpaRepository.findAll();
        return exerciseFound.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Exercise> findByMuscleGroupId(Long muscleGroupId) {
        List<ExerciseJpaEntity> exerciseFound = jpaRepository.findByMuscleGroupIdAndActiveTrue(muscleGroupId);
        return exerciseFound.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Exercise> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Exercise> findAllowedForGym(Long gymId, Long muscleGroupId) {
        return jpaRepository.findAllowedForGymAndActiveTrue(gymId, muscleGroupId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameAndGymId(String name, Long gymId) {
        return jpaRepository.existsByNameAndGymIdAndActiveTrue(name, gymId);
    }

    @Override
    public boolean existsBaseExerciseByName(String name) {
        return jpaRepository.existsByNameAndIsBaseTrueAndActiveTrue(name);
    }

    @Override
    public boolean existsByNameAndGymIdAndIdNot(String name, Long gymId, Long id) {
        return jpaRepository.existsByNameAndGymIdAndIdNotAndActiveTrue(name, gymId, id);
    }

    @Override
    public boolean existsBaseExerciseByNameAndIdNot(String name, Long id) {
        return jpaRepository.existsByNameAndIsBaseTrueAndIdNotAndActiveTrue(name, id);
    }

    @Override
    public List<Exercise> findByGymId(Long gymId) {
        return jpaRepository.findByGymIdAndActiveTrue(gymId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}