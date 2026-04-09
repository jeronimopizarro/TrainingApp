package com.trainingapp.trainingapp.domain.entity.gym;

import com.trainingapp.trainingapp.domain.exception.gym.GymAddressRequiredException;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyActiveException;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyInactiveException;
import com.trainingapp.trainingapp.domain.exception.gym.GymNameRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GymTest {

    @Test
    @DisplayName("Debería crear un gimnasio válido con estado activo")
    void shouldCreateValidGym() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");

        assertNotNull(gym);
        assertEquals("Iron Temple", gym.getName());
        assertEquals("Main St 123", gym.getAddress());
        assertEquals("555-1234", gym.getPhoneNumber());
        assertTrue(gym.isActive());
    }

    @Test
    @DisplayName("Debería lanzar error si el nombre del gimnasio es nulo o vacío")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(GymNameRequiredException.class, () ->
                Gym.createNew(null, "Main St 123", "555-1234")
        );

        assertThrows(GymNameRequiredException.class, () ->
                Gym.createNew("", "Main St 123", "555-1234")
        );

        assertThrows(GymNameRequiredException.class, () ->
                Gym.createNew("   ", "Main St 123", "555-1234")
        );
    }

    @Test
    @DisplayName("Debería lanzar error si la dirección del gimnasio es nula o vacía")
    void shouldThrowExceptionWhenAddressIsInvalid() {
        assertThrows(GymAddressRequiredException.class, () ->
                Gym.createNew("Iron Temple", null, "555-1234")
        );

        assertThrows(GymAddressRequiredException.class, () ->
                Gym.createNew("Iron Temple", "", "555-1234")
        );

        assertThrows(GymAddressRequiredException.class, () ->
                Gym.createNew("Iron Temple", "   ", "555-1234")
        );
    }

    @Test
    @DisplayName("Debería actualizar los detalles del gimnasio exitosamente")
    void shouldUpdateGymDetails() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");

        gym.updateDetails("Steel Temple", "New Ave 456", "111-9999");

        assertEquals("Steel Temple", gym.getName());
        assertEquals("New Ave 456", gym.getAddress());
        assertEquals("111-9999", gym.getPhoneNumber());
    }

    @Test
    @DisplayName("Debería lanzar error al actualizar con datos inválidos")
    void shouldThrowExceptionWhenUpdatingWithInvalidData() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");

        assertThrows(GymNameRequiredException.class, () ->
                gym.updateDetails(null, "New Ave 456", "111-9999")
        );

        assertThrows(GymAddressRequiredException.class, () ->
                gym.updateDetails("Steel Temple", "", "111-9999")
        );
    }

    @Test
    @DisplayName("Debería desactivar un gimnasio exitosamente")
    void shouldDeactivateGym() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");

        gym.deactivate();

        assertFalse(gym.isActive());
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta desactivar un gimnasio ya inactivo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveGym() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");
        gym.deactivate();

        assertThrows(GymAlreadyInactiveException.class, gym::deactivate);
    }

    @Test
    @DisplayName("Debería reactivar un gimnasio exitosamente")
    void shouldReactivateGym() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");
        gym.deactivate();

        gym.activate();

        assertTrue(gym.isActive());
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta reactivar un gimnasio ya activo")
    void shouldThrowExceptionWhenReactivatingAlreadyActiveGym() {
        Gym gym = Gym.createNew("Iron Temple", "Main St 123", "555-1234");

        assertThrows(GymAlreadyActiveException.class, gym::activate);
    }
}