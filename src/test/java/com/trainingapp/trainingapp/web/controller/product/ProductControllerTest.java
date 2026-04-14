package com.trainingapp.trainingapp.web.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingapp.trainingapp.application.useCase.product.*;
import com.trainingapp.trainingapp.config.TestSecurityConfig;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.CustomUserDetailsService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureJson
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Dependencias de los Casos de Uso
    @MockitoBean private CreateProductUseCase createProductUseCase;
    @MockitoBean private UpdateProductUseCase updateProductUseCase;
    @MockitoBean private GetAllProductsByGymIdUseCase getAllProductsByGymIdUseCase;
    @MockitoBean private GetProductByIdUseCase getProductByIdUseCase;
    @MockitoBean private SearchProductsByNameUseCase searchProductsByNameUseCase;
    @MockitoBean private DeleteProductUseCase deleteProductUseCase;

    // Dependencias de Seguridad
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("POST /products - Debería retornar 201 Created al crear un producto")
    void shouldCreateProduct() throws Exception {
        // Constructores actualizados
        CreateProductRequest request = new CreateProductRequest("Agua", "Botella 500ml", new BigDecimal("500.0"), 50, "url", 10L);
        ProductResponse mockResponse = new ProductResponse(1L, "Agua", "Botella 500ml", new BigDecimal("500.0"), 50, "url", true, 10L);

        when(createProductUseCase.execute(any(CreateProductRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Agua"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("PUT /products/{id} - Debería retornar 200 OK al actualizar")
    void shouldUpdateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Agua Editada", "Botella 500ml", new BigDecimal("600.0"), 60, "url", 10L);
        ProductResponse mockResponse = new ProductResponse(1L, "Agua Editada", "Botella 500ml", new BigDecimal("600.0"), 60, "url", true, 10L);

        when(updateProductUseCase.execute(eq(1L), any(CreateProductRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Agua Editada"));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /products/gym/{gymId} - Debería retornar 200 OK")
    void shouldGetAllProducts() throws Exception {
        when(getAllProductsByGymIdUseCase.execute(eq(10L), any())).thenReturn(List.of());

        mockMvc.perform(get("/products/gym/10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /products/{id} - Debería retornar 200 OK y el producto solicitado")
    void shouldGetProductById() throws Exception {
        // Constructor actualizado
        ProductResponse mockResponse = new ProductResponse(1L, "Agua", "Botella 500ml", new BigDecimal("500.0"), 50, "url", true, 10L);
        when(getProductByIdUseCase.execute(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("GET /products/gym/{gymId}/search?name=Agua - Debería retornar 200 OK")
    void shouldSearchProducts() throws Exception {
        // Ajustado: execute(Long gymId, String name)
        when(searchProductsByNameUseCase.execute(10L, "Agua")).thenReturn(List.of());

        mockMvc.perform(get("/products/gym/10/search")
                        .param("name", "Agua"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GYM_ADMIN")
    @DisplayName("DELETE /products/{id} - Debería retornar 204 No Content al eliminar")
    void shouldDeleteProduct() throws Exception {
        doNothing().when(deleteProductUseCase).execute(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}