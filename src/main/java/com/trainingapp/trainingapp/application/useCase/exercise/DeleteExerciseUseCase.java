package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.validator.ExerciseAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseAccessValidator exerciseAccessValidator;

    public DeleteExerciseUseCase(ExerciseRepository exerciseRepository,
                                 ExerciseAccessValidator exerciseAccessValidator) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseAccessValidator = exerciseAccessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Exercise exercise = findExerciseOrThrow(id);

        exerciseAccessValidator.validateWriteAccess(exercise);

        exercise.deactivate();
        exerciseRepository.save(exercise);
    }

    private Exercise findExerciseOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
    }
}