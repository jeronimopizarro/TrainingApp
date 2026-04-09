package com.trainingapp.trainingapp.application.useCase.exercise.muscleGroup;

import com.trainingapp.trainingapp.application.useCase.exercise.GetAllMuscleGroupsUseCase;
import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllMuscleGroupsUseCaseTest {

    @Mock
    private MuscleGroupRepository muscleGroupRepository;

    @InjectMocks
    private GetAllMuscleGroupsUseCase useCase;

    @Test
    @DisplayName("Debería retornar todos los grupos musculares disponibles")
    void shouldReturnAllMuscleGroups() {
        MuscleGroup mockMuscleGroup = mock(MuscleGroup.class);

        when(muscleGroupRepository.findAll()).thenReturn(List.of(mockMuscleGroup));
        when(mockMuscleGroup.getId()).thenReturn(1L);
        when(mockMuscleGroup.getName()).thenReturn("Espalda");
        when(mockMuscleGroup.getDescription()).thenReturn("Músculos dorsales");

        List<MuscleGroupResponse> responses = useCase.execute();

        assertEquals(1, responses.size());
        assertEquals("Espalda", responses.get(0).name());
    }
}