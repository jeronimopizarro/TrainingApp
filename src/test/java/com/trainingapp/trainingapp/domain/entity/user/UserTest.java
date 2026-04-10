package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // Implementación concreta para poder instanciar y testear la clase abstracta User
    private static class DummyUser extends User {
        public DummyUser(Long id, String firstName, String lastName, String email, String password, String dni, Role role, boolean active) {
            super(id, firstName, lastName, email, password, dni, role, active);
        }
    }

    @Test
    @DisplayName("Debería crear un usuario base válido")
    void shouldCreateValidBaseUser() {
        User user = new DummyUser(1L, "Juan", "Perez", "juan@test.com", "pass123", "12345678", Role.MEMBER, true);

        assertNotNull(user);
        assertEquals("Juan", user.getFirstName());
        assertEquals("Perez", user.getLastName());
        assertEquals("juan@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("12345678", user.getDni());
        assertEquals(Role.MEMBER, user.getRole());
        assertTrue(user.isActive());
        assertTrue(user.isMember());
        assertFalse(user.isSuperAdmin());
    }

    @Test
    @DisplayName("Debería lanzar error si el email es inválido o nulo")
    void shouldThrowExceptionOnInvalidEmail() {
        assertThrows(InvalidEmailException.class, () ->
                new DummyUser(1L, "Juan", "Perez", "emailinvalido", "pass", "123", Role.MEMBER, true));
        assertThrows(InvalidEmailException.class, () ->
                new DummyUser(1L, "Juan", "Perez", "", "pass", "123", Role.MEMBER, true));
    }

    @Test
    @DisplayName("Debería lanzar error si faltan campos obligatorios (nombre, apellido, password, dni)")
    void shouldThrowExceptionOnMissingRequiredFields() {
        assertThrows(UserFirstNameRequiredException.class, () ->
                new DummyUser(1L, "", "Perez", "j@t.com", "pass", "123", Role.MEMBER, true));

        assertThrows(UserLastNameRequiredException.class, () ->
                new DummyUser(1L, "Juan", null, "j@t.com", "pass", "123", Role.MEMBER, true));

        assertThrows(UserPasswordRequiredException.class, () ->
                new DummyUser(1L, "Juan", "Perez", "j@t.com", "  ", "123", Role.MEMBER, true));

        assertThrows(UserDniRequiredException.class, () ->
                new DummyUser(1L, "Juan", "Perez", "j@t.com", "pass", null, Role.MEMBER, true));
    }

    @Test
    @DisplayName("Debería actualizar los detalles base exitosamente")
    void shouldUpdateBaseDetails() {
        User user = new DummyUser(1L, "Juan", "Perez", "juan@test.com", "pass123", "12345678", Role.MEMBER, true);

        user.updateBaseDetails("Carlos", "Gomez", "87654321");

        assertEquals("Carlos", user.getFirstName());
        assertEquals("Gomez", user.getLastName());
        assertEquals("87654321", user.getDni());
    }

    @Test
    @DisplayName("Debería desactivar y reactivar al usuario validando los estados")
    void shouldDeactivateAndReactivateUser() {
        User user = new DummyUser(1L, "Juan", "Perez", "juan@test.com", "pass123", "12345678", Role.MEMBER, true);

        user.deactivate();
        assertFalse(user.isActive());
        assertThrows(UserAlreadyInactiveException.class, user::deactivate);

        user.activate();
        assertTrue(user.isActive());
        assertThrows(UserAlreadyActiveException.class, user::activate);
    }
}