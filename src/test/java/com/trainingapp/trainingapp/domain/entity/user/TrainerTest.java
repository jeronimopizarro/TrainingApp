package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    @Test
    @DisplayName("Debería crear un Trainer válido usando createNew")
    void shouldCreateValidTrainer() {
        Long gymId = 10L;

        Trainer trainer = Trainer.createNew(
                "Profe", "Dos", "profe@test.com", "pass123", "44444444",
                gymId, "Experto en pesas"
        );

        assertNotNull(trainer);
        assertNull(trainer.getId());
        assertEquals("Profe", trainer.getFirstName());
        assertEquals("Dos", trainer.getLastName());
        assertEquals(gymId, trainer.getGymId());
        assertEquals("Experto en pesas", trainer.getSpecialization());
        assertTrue(trainer.isActive());
        assertTrue(trainer.isTrainer());
        assertEquals(Role.TRAINER, trainer.getRole());
    }

    @Test
    @DisplayName("Debería restaurar un Trainer desde la base de datos")
    void shouldRestoreTrainer() {
        Long id = 1L;
        Long gymId = 10L;

        Trainer trainer = Trainer.restore(
                id, "Profe", "Viejo", "old@test.com", "pass", "444",
                Role.TRAINER, false, gymId, "Cardio"
        );

        assertNotNull(trainer);
        assertEquals(id, trainer.getId());
        assertEquals(gymId, trainer.getGymId());
        assertEquals("Cardio", trainer.getSpecialization());
        assertFalse(trainer.isActive());
    }

    @Test
    @DisplayName("Debería actualizar los detalles del Trainer exitosamente")
    void shouldUpdateTrainerDetails() {
        Trainer trainer = Trainer.createNew(
                "Profe", "Dos", "profe@test.com", "pass123", "44444444",
                10L, "Pesas"
        );

        trainer.updateTrainerDetails("Carlos", "Gomez", "55555555", "Crossfit");

        assertEquals("Carlos", trainer.getFirstName());
        assertEquals("Gomez", trainer.getLastName());
        assertEquals("55555555", trainer.getDni());
        assertEquals("Crossfit", trainer.getSpecialization());
    }
}