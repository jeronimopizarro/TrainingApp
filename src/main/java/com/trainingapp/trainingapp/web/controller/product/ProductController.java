package com.trainingapp.trainingapp.web.controller.product;

import com.trainingapp.trainingapp.application.useCase.product.*;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetAllProductsByGymIdUseCase getAllProductsByGymIdUseCase;
    private final SearchProductsByNameUseCase searchProductsByNameUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             GetProductByIdUseCase getProductByIdUseCase,
                             GetAllProductsByGymIdUseCase getAllProductsByGymIdUseCase,
                             SearchProductsByNameUseCase searchProductsByNameUseCase,
                             DeleteProductUseCase deleteProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getAllProductsByGymIdUseCase = getAllProductsByGymIdUseCase;
        this.searchProductsByNameUseCase = searchProductsByNameUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = createProductUseCase.execute(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = updateProductUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(getProductByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByGymId(
            @PathVariable Long gymId,
            @RequestParam(required = false) String stockStatus) {
        return ResponseEntity.ok(getAllProductsByGymIdUseCase.execute(gymId, stockStatus));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'RECEPTIONIST')")
    @GetMapping("/gym/{gymId}/search")
    public ResponseEntity<List<ProductResponse>> searchProductsByName(
            @PathVariable Long gymId,
            @RequestParam String name) {
        return ResponseEntity.ok(searchProductsByNameUseCase.execute(gymId, name));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}