package com.trainingapp.trainingapp.application.useCase.user;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveInactiveUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArchiveInactiveUsersUseCase useCase;

    @Test
    @DisplayName("Debería desactivar usuarios inactivos y guardarlos")
    void shouldDeactivateInactiveUsers() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);

        when(userRepository.findUsersWithoutAccessSince(any(LocalDateTime.class)))
                .thenReturn(List.of(mockUser1, mockUser2));

        useCase.execute();

        verify(mockUser1).deactivate();
        verify(mockUser2).deactivate();
        verify(userRepository).save(mockUser1);
        verify(userRepository).save(mockUser2);
    }

    @Test
    @DisplayName("No debería hacer nada si la lista de usuarios inactivos está vacía")
    void shouldDoNothingWhenNoInactiveUsers() {
        when(userRepository.findUsersWithoutAccessSince(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        useCase.execute();

        verify(userRepository, never()).save(any(User.class));
    }
}