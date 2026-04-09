package com.trainingapp.trainingapp.web.controller.exercise;

import com.trainingapp.trainingapp.application.useCase.exercise.GetAllMuscleGroupsUseCase;
import com.trainingapp.trainingapp.application.useCase.exercise.GetMuscleGroupByIdUseCase;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.exercise.MuscleGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MuscleGroupController.class)
@Import(TestSecurityConfig.class)
class MuscleGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllMuscleGroupsUseCase getAllUseCase;

    @MockitoBean
    private GetMuscleGroupByIdUseCase getByIdUseCase;

    // Dependencias de seguridad
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("GET /muscle-groups - Debería retornar 200 OK y la lista de grupos")
    void shouldGetAllMuscleGroups() throws Exception {
        when(getAllUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/muscle-groups"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("GET /muscle-groups/{id} - Debería retornar 200 OK y el grupo muscular solicitado")
    void shouldGetMuscleGroupById() throws Exception {
        MuscleGroupResponse mockResponse = new MuscleGroupResponse(1L, "Pecho", "Descripción");

        when(getByIdUseCase.execute(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/muscle-groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pecho"));
    }
}