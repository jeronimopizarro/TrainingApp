package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.exception.user.EmailAlreadyExistsException;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRegistrationValidator validator;

    @Test
    @DisplayName("Debería pasar si el email es único")
    void shouldPassIfEmailIsUnique() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        assertDoesNotThrow(() -> validator.validateEmailIsUnique("test@test.com"));
    }

    @Test
    @DisplayName("Debería lanzar error si el email ya existe")
    void shouldThrowIfEmailExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> validator.validateEmailIsUnique("test@test.com"));
    }
}