package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistTest {

    @Test
    @DisplayName("Debería crear un Receptionist válido usando createNew")
    void shouldCreateValidReceptionist() {
        Long gymId = 10L;

        Receptionist receptionist = Receptionist.createNew(
                "Recep", "Tres", "recep@test.com", "pass123", "55555555", gymId
        );

        assertNotNull(receptionist);
        assertNull(receptionist.getId());
        assertEquals("Recep", receptionist.getFirstName());
        assertEquals(gymId, receptionist.getGymId());
        assertTrue(receptionist.isActive());
        assertEquals(Role.RECEPTIONIST, receptionist.getRole());
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta crear un Receptionist sin gymId")
    void shouldThrowExceptionWhenGymIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Receptionist.createNew("Recep", "Tres", "recep@test.com", "pass123", "55555555", null)
        );

        assertEquals("El recepcionista debe estar asignado a un gimnasio.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería restaurar un Receptionist desde la base de datos")
    void shouldRestoreReceptionist() {
        Long id = 1L;
        Long gymId = 10L;

        Receptionist receptionist = Receptionist.restore(
                id, "Recep", "Viejo", "old@test.com", "pass", "555", false, gymId
        );

        assertNotNull(receptionist);
        assertEquals(id, receptionist.getId());
        assertEquals(gymId, receptionist.getGymId());
        assertFalse(receptionist.isActive());
    }

    @Test
    @DisplayName("Debería actualizar el perfil del Receptionist exitosamente")
    void shouldUpdateReceptionistProfile() {
        Receptionist receptionist = Receptionist.createNew(
                "Recep", "Tres", "recep@test.com", "pass123", "55555555", 10L
        );

        receptionist.updateProfile("Carlos", "Gomez", "66666666");

        assertEquals("Carlos", receptionist.getFirstName());
        assertEquals("Gomez", receptionist.getLastName());
        assertEquals("66666666", receptionist.getDni());
    }
}