package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    @DisplayName("Debería crear un Super Admin válido usando createNew")
    void shouldCreateValidSuperAdmin() {
        Admin admin = Admin.createNew(
                "Super", "Admin", "super@test.com", "pass123", "11111111", Role.SUPER_ADMIN, null
        );

        assertNotNull(admin);
        assertNull(admin.getId()); // Creado nuevo, no tiene ID todavía
        assertEquals("Super", admin.getFirstName());
        assertEquals("Admin", admin.getLastName());
        assertEquals("super@test.com", admin.getEmail());
        assertEquals("pass123", admin.getPassword());
        assertEquals("11111111", admin.getDni());
        assertEquals(Role.SUPER_ADMIN, admin.getRole());
        assertNull(admin.getGymId()); // Super Admin no está atado a un gimnasio específico
        assertTrue(admin.isActive());
        assertTrue(admin.isSuperAdmin());
        assertFalse(admin.isGymAdmin());
    }

    @Test
    @DisplayName("Debería crear un Gym Admin válido usando createNew asociado a un Gym")
    void shouldCreateValidGymAdmin() {
        Long gymId = 10L;
        Admin admin = Admin.createNew(
                "Gym", "Admin", "gym@test.com", "pass123", "22222222", Role.GYM_ADMIN, gymId
        );

        assertNotNull(admin);
        assertNull(admin.getId());
        assertEquals("Gym", admin.getFirstName());
        assertEquals(Role.GYM_ADMIN, admin.getRole());
        assertEquals(gymId, admin.getGymId());
        assertTrue(admin.isActive());
        assertTrue(admin.isGymAdmin());
        assertFalse(admin.isSuperAdmin());
    }

    @Test
    @DisplayName("Debería restaurar un Admin desde la base de datos manteniendo sus valores")
    void shouldRestoreAdmin() {
        Long id = 1L;
        Long gymId = 10L;
        Admin admin = Admin.restore(
                id, "Old", "Admin", "old@test.com", "pass123", "33333333", Role.GYM_ADMIN, false, gymId
        );

        assertNotNull(admin);
        assertEquals(id, admin.getId());
        assertEquals("Old", admin.getFirstName());
        assertEquals("Admin", admin.getLastName());
        assertEquals("old@test.com", admin.getEmail());
        assertEquals("pass123", admin.getPassword());
        assertEquals("33333333", admin.getDni());
        assertEquals(Role.GYM_ADMIN, admin.getRole());
        assertEquals(gymId, admin.getGymId());
        assertFalse(admin.isActive()); // Verificamos que restaura el estado inactivo correctamente
    }
}