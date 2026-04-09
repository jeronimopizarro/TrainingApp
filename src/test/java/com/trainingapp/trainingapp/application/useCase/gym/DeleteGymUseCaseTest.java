package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGymUseCaseTest {

    @Mock private GymRepository gymRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private MembershipPlanRepository membershipPlanRepository;
    @Mock private ExerciseRepository exerciseRepository;

    @InjectMocks private DeleteGymUseCase useCase;

    @Test
    @DisplayName("Debería desactivar el gimnasio y todas sus entidades relacionadas en cascada")
    void shouldDeactivateGymAndAllRelatedEntities() {
        Long gymId = 1L;
        Gym mockGym = mock(Gym.class);
        Admin mockAdmin = mock(Admin.class);
        Trainer mockTrainer = mock(Trainer.class);
        Member mockMember = mock(Member.class);
        MembershipPlan mockPlan = mock(MembershipPlan.class);
        Exercise mockExercise = mock(Exercise.class);

        when(gymRepository.findById(gymId)).thenReturn(Optional.of(mockGym));

        when(adminRepository.findByGymId(gymId)).thenReturn(List.of(mockAdmin));
        when(trainerRepository.findByGymId(gymId)).thenReturn(List.of(mockTrainer));
        when(memberRepository.findByGymId(gymId)).thenReturn(List.of(mockMember));
        when(membershipPlanRepository.findByGymId(gymId)).thenReturn(List.of(mockPlan));
        when(exerciseRepository.findByGymId(gymId)).thenReturn(List.of(mockExercise));

        useCase.execute(gymId);

        // Verificamos desactivaciones
        verify(mockGym).deactivate();
        verify(mockAdmin).deactivate();
        verify(mockTrainer).deactivate();
        verify(mockMember).deactivate();
        verify(mockPlan).deactivate();
        verify(mockExercise).deactivate();

        // Verificamos guardados
        verify(gymRepository).save(mockGym);
        verify(adminRepository).save(mockAdmin);
        verify(trainerRepository).save(mockTrainer);
        verify(memberRepository).save(mockMember);
        verify(membershipPlanRepository).save(mockPlan);
        verify(exerciseRepository).save(mockExercise);
    }
}