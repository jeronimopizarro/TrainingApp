package com.trainingapp.trainingapp.application.useCase.exercise.muscleGroup;

import com.trainingapp.trainingapp.application.useCase.exercise.GetMuscleGroupByIdUseCase;
import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.exception.exercise.MuscleGroupNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMuscleGroupByIdUseCaseTest {

    @Mock
    private MuscleGroupRepository muscleGroupRepository;

    @InjectMocks
    private GetMuscleGroupByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un grupo muscular exitosamente por su ID")
    void shouldReturnMuscleGroupById() {
        Long id = 1L;
        MuscleGroup mockMuscleGroup = mock(MuscleGroup.class);

        when(muscleGroupRepository.findById(id)).thenReturn(Optional.of(mockMuscleGroup));
        when(mockMuscleGroup.getId()).thenReturn(id);
        when(mockMuscleGroup.getName()).thenReturn("Pecho");
        when(mockMuscleGroup.getDescription()).thenReturn("Músculos pectorales");

        MuscleGroupResponse response = useCase.execute(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Pecho", response.name());
        assertEquals("Músculos pectorales", response.description());
    }

    @Test
    @DisplayName("Debería lanzar MuscleGroupNotFoundException si el ID no existe")
    void shouldThrowExceptionWhenNotFound() {
        Long id = 99L;

        when(muscleGroupRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MuscleGroupNotFoundException.class, () -> useCase.execute(id));
    }
}