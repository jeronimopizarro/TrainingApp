package com.trainingapp.trainingapp.domain.entity.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.exception.access.InvalidAccessLogException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccessLogTest {

    @Test
    @DisplayName("Debería crear un registro de acceso exitoso (Granted) válido")
    void shouldCreateValidGrantedAccessLog() {
        Long memberId = 100L;
        Long gymId = 10L;
        String message = "Acceso permitido";

        AccessLog accessLog = AccessLog.createNew(memberId, gymId, true, message);

        assertNotNull(accessLog);
        assertNull(accessLog.getId());
        assertEquals(memberId, accessLog.getMemberId());
        assertEquals(gymId, accessLog.getGymId());
        assertTrue(accessLog.isAccessGranted());
        assertEquals(message, accessLog.getMessage());
        assertNotNull(accessLog.getTimestamp());
    }

    @Test
    @DisplayName("Debería crear un registro de acceso denegado (Denied) válido")
    void shouldCreateValidDeniedAccessLog() {
        Long memberId = 100L;
        Long gymId = 10L;
        String message = "Suscripción expirada";

        AccessLog accessLog = AccessLog.createNew(memberId, gymId, false, message);

        assertNotNull(accessLog);
        assertNull(accessLog.getId());
        assertEquals(memberId, accessLog.getMemberId());
        assertEquals(gymId, accessLog.getGymId());
        assertFalse(accessLog.isAccessGranted());
        assertEquals(message, accessLog.getMessage());
        assertNotNull(accessLog.getTimestamp());
    }

    @Test
    @DisplayName("Debería restaurar un registro de acceso desde la base de datos")
    void shouldRestoreAccessLog() {
        Long id = 1L;
        Long memberId = 100L;
        Long gymId = 10L;
        LocalDateTime timestamp = LocalDateTime.now().minusDays(1);
        String message = "Acceso permitido";

        AccessLog accessLog = AccessLog.restore(id, memberId, gymId, timestamp, true, message);

        assertNotNull(accessLog);
        assertEquals(id, accessLog.getId());
        assertEquals(memberId, accessLog.getMemberId());
        assertEquals(gymId, accessLog.getGymId());
        assertEquals(timestamp, accessLog.getTimestamp());
        assertTrue(accessLog.isAccessGranted());
        assertEquals(message, accessLog.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar error si se crea un registro de acceso sin memberId")
    void shouldThrowExceptionWhenMemberIdIsMissing() {
        Long gymId = 10L;
        String message = "Acceso denegado";

        InvalidAccessLogException exception = assertThrows(InvalidAccessLogException.class, () ->
                AccessLog.createNew(null, gymId, false, message)
        );

        assertEquals("El registro de acceso debe estar asociado a un socio.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar error si se crea un registro de acceso sin gymId")
    void shouldThrowExceptionWhenGymIdIsMissing() {
        Long memberId = 100L;
        String message = "Acceso denegado";

        InvalidAccessLogException exception = assertThrows(InvalidAccessLogException.class, () ->
                AccessLog.createNew(memberId, null, false, message)
        );

        assertEquals("El registro de acceso debe indicar en qué gimnasio ocurrió.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta restaurar un registro de acceso sin timestamp")
    void shouldThrowExceptionWhenTimestampIsMissing() {
        Long id = 1L;
        Long memberId = 100L;
        Long gymId = 10L;
        String message = "Acceso permitido";

        InvalidAccessLogException exception = assertThrows(InvalidAccessLogException.class, () ->
                AccessLog.restore(id, memberId, gymId, null, true, message)
        );

        assertEquals("El registro de acceso debe tener una fecha y hora exacta.", exception.getMessage());
    }
}