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

    public ExerciseRepositoryImpl(ExerciseJpaRepository jpaRepository, ExerciseMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Exercise save(Exercise domain) {
        ExerciseJpaEntity entityToSave;

        if (domain.getId() != null) {
            entityToSave = jpaRepository.findById(domain.getId())
                    .orElse(new ExerciseJpaEntity());

            updateEntityFromDomain(entityToSave, domain);
        } else {
            entityToSave = mapper.toEntity(domain);
        }
        // 2. Usamos saveAndFlush para obligar a Hibernate a sincronizar los borrados del .clear()
        ExerciseJpaEntity savedEntity = jpaRepository.saveAndFlush(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    private void updateEntityFromDomain(ExerciseJpaEntity entity, Exercise domain) {
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setImageUrl(domain.getImageUrl());
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setIsBase(domain.getIsBase());

        entity.getMuscleGroups().clear();

        if (domain.getMuscleGroups() != null) {
            domain.getMuscleGroups().forEach(mgDomain -> {
                ExerciseMuscleGroupJpaEntity mgEntity = new ExerciseMuscleGroupJpaEntity();

                MuscleGroupJpaEntity muscle = new MuscleGroupJpaEntity();
                muscle.setId(mgDomain.getMuscleGroupId());

                mgEntity.setMuscleGroup(muscle);
                mgEntity.setExercise(entity);
                mgEntity.setPrimary(mgDomain.isPrimary());

                // Seteamos manualmente el ID compuesto para evitar que Hibernate se pierda
                mgEntity.getId().setExerciseId(entity.getId());
                mgEntity.getId().setMuscleGroupId(mgDomain.getMuscleGroupId());

                entity.getMuscleGroups().add(mgEntity);
            });
        }
    }

    @Override
    public Optional<Exercise> findById(long id) {
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
        List<ExerciseJpaEntity> exerciseFound = jpaRepository.findByMuscleGroupId(muscleGroupId);
        return exerciseFound.stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(Exercise exercise) {
        jpaRepository.deleteById(exercise.getId());
    }

    @Override
    public List<Exercise> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}