# TrainingApp - Sistema de Gestión de Entrenamientos

**TrainingApp** es una robusta API REST diseñada para la gestión integral de gimnasios, rutinas personalizadas y seguimiento de progreso. Este proyecto fue desarrollado como parte de un portafolio para demostrar habilidades avanzadas en el ecosistema **Java/Spring** y la aplicación de principios de ingeniería de software.

---

## Características Principales

*   **Gestión Multi-Gimnasio:** Soporte para múltiples sucursales con aislamiento de datos.
*   **Sistema de Rutinas Dinámicas:** Creación de rutinas por entrenadores o usuarios, con días de entrenamiento y ejercicios específicos.
*   **Tracker de Entrenamiento:** Registro de series, repeticiones, peso y cálculo automático de **e1RM (1RM estimado)** para medir el progreso.
*   **Control de Acceso (QR):** Generación y validación de tokens QR para el ingreso al gimnasio.
*   **Gestión de Membresías:** Control de suscripciones activas/vencidas y procesamiento de transacciones.
*   **Dashboard Inteligente:** Métricas personalizadas para Administradores, Entrenadores y Miembros.

---

## Arquitectura y Patrones

El proyecto destaca por el uso de **Arquitectura Hexagonal (Clean Architecture)** y principios de **DDD (Domain-Driven Design)**:

*   **Capa de Dominio:** Entidades ricas en lógica de negocio (no anémicas), objetos de valor y reglas de validación intrínsecas.
*   **Capa de Aplicación:** Implementación de Casos de Uso independientes de la infraestructura.
*   **Capa de Infraestructura:** Desacoplamiento total de la base de datos (JPA/MySQL) y seguridad (JWT).
* **Principios SOLID:** Código altamente mantenible, testeable y escalable.

---

## 📊 Modelo de Datos / DER

A continuación, se presenta el modelo relacional simplificado de la aplicación, mostrando cómo interactúan los diferentes módulos (Gimnasios, Usuarios, Rutinas, Tracker y Finanzas):

```mermaid
erDiagram
    GYM ||--o{ USER : "pertenece a"
    GYM ||--o{ EXERCISE : "ofrece"
    GYM ||--o{ PRODUCT : "vende"
    GYM ||--o{ MEMBERSHIP_PLAN : "configura"

    USER ||--o{ ACCESS_LOG : "registra"

    USER <|-- MEMBER : "es un"
    USER <|-- TRAINER : "es un"
    USER <|-- ADMIN : "es un"

    MEMBER ||--o{ SUBSCRIPTION : "tiene"
    SUBSCRIPTION }|--|| MEMBERSHIP_PLAN : "basada en"

    TRAINER ||--o{ ROUTINE : "asigna"
    MEMBER ||--o{ ROUTINE : "entrena con"

    ROUTINE ||--o{ TRAINING_DAY : "contiene"
    TRAINING_DAY ||--o{ ROUTINE_DETAIL : "define"
    ROUTINE_DETAIL }|--|| EXERCISE : "usa"

    MEMBER ||--o{ TRAINING_SESSION : "realiza"
    TRAINING_SESSION ||--o{ SET_LOG : "registra"
    SET_LOG }|--|| EXERCISE : "de"

    GYM ||--o{ TRANSACTION : "registra flujo de"
    TRANSACTION ||--o? SALE : "vinculada a"
    TRANSACTION ||--o? SUBSCRIPTION : "vinculada a"

    SALE ||--o{ SALE_DETAIL : "contiene"
    SALE_DETAIL }|--|| PRODUCT : "incluye"
```

---

## 🛠️ Stack Tecnológico

*   **Lenguaje:** Java 17
*   **Framework:** Spring Boot 3.4.1
*   **Persistencia:** Spring Data JPA / MySQL
*   **Seguridad:** Spring Security + JWT (JSON Web Tokens)
*   **Validación:** Hibernate Validator / Jakarta Validation
*   **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers
*   **Herramientas:** Maven, Lombok

---

## Calidad y Testing

Se ha priorizado la fiabilidad del sistema mediante una estrategia de pruebas exhaustiva:
*   **Unit Tests:** Pruebas de lógica de dominio y casos de uso.
*   **Integration Tests:** Validación de repositorios y controladores.
*   **E2E Tests:** Pruebas de flujo de negocio completo (Login -> Crear Rutina -> Registrar Entrenamiento).

---

## Configuración del Proyecto

### Requisitos
*   JDK 17 o superior.
*   Maven 3.8+.
*   MySQL 8.0+.

### Instalación
1. Clonar el repositorio.
2. Configurar las credenciales de base de datos en `src/main/resources/application.properties`.
3. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

---
