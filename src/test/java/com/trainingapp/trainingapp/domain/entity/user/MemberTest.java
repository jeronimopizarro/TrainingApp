package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("Debería crear un Member válido usando createNew")
    void shouldCreateValidMember() {
        Long gymId = 10L;
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        Member member = Member.createNew(
                "Socio", "Uno", "socio@test.com", "pass123", "33333333",
                gymId, birthDate, "Ganar masa muscular"
        );

        assertNotNull(member);
        assertNull(member.getId());
        assertEquals("Socio", member.getFirstName());
        assertEquals("Uno", member.getLastName());
        assertEquals(gymId, member.getGymId());
        assertEquals(birthDate, member.getBirthDate());
        assertEquals("Ganar masa muscular", member.getPrimaryGoal());
        assertTrue(member.isActive());
        assertTrue(member.isMember());
        assertEquals(Role.MEMBER, member.getRole());
    }

    @Test
    @DisplayName("Debería restaurar un Member desde la base de datos")
    void shouldRestoreMember() {
        Long id = 1L;
        Long gymId = 10L;
        LocalDate birthDate = LocalDate.of(1995, 10, 20);

        Member member = Member.restore(
                id, "Socio", "Viejo", "old@test.com", "pass", "333",
                Role.MEMBER, false, gymId, birthDate, "Perder peso"
        );

        assertNotNull(member);
        assertEquals(id, member.getId());
        assertEquals(gymId, member.getGymId());
        assertFalse(member.isActive());
        assertEquals("Perder peso", member.getPrimaryGoal());
    }

    @Test
    @DisplayName("Debería actualizar los detalles del Member exitosamente")
    void shouldUpdateMemberDetails() {
        Member member = Member.createNew(
                "Socio", "Uno", "socio@test.com", "pass123", "33333333",
                10L, LocalDate.of(1990, 5, 15), "Ganar masa"
        );

        LocalDate newBirthDate = LocalDate.of(1991, 6, 16);
        member.updateMemberDetails("Carlos", "Gomez", "44444444", newBirthDate, "Mantenimiento");

        assertEquals("Carlos", member.getFirstName());
        assertEquals("Gomez", member.getLastName());
        assertEquals("44444444", member.getDni());
        assertEquals(newBirthDate, member.getBirthDate());
        assertEquals("Mantenimiento", member.getPrimaryGoal());
    }
}