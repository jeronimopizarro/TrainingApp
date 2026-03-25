package com.trainingapp.trainingapp.application.useCase.sale;

import com.trainingapp.trainingapp.application.mapper.sale.SaleDTOMapper;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionCommand;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionUseCase;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.entity.sale.SaleDetail;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.domain.repository.sale.SaleRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.sale.CreateSaleRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleDetailRequest;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessSaleUseCase {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final SecurityUtils securityUtils;
    private final GymValidator gymValidator;
    private final SaleDTOMapper saleDTOMapper;

    public ProcessSaleUseCase(SaleRepository saleRepository,
                              ProductRepository productRepository,
                              RegisterTransactionUseCase registerTransactionUseCase,
                              SecurityUtils securityUtils,
                              GymValidator gymValidator,
                              SaleDTOMapper saleDTOMapper) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.securityUtils = securityUtils;
        this.gymValidator = gymValidator;
        this.saleDTOMapper = saleDTOMapper;
    }

    @Transactional
    public SaleResponse execute(CreateSaleRequest request) {
        Long currentGymId = securityUtils.getCurrentUserGymId();
        Long currentAdminId = securityUtils.getCurrentUser().getId();

        validateGymAccess(currentGymId);

        List<SaleDetail> details = createSaleDetails(request.details());
        Sale savedSale = persistSale(request, currentGymId, currentAdminId, details);
        registerIncomeInLedger(savedSale, currentGymId, currentAdminId);

        return saleDTOMapper.toResponse(savedSale);
    }

    private void validateGymAccess(Long gymId) {
        if (gymId != null) {
            gymValidator.validateExists(gymId);
        }
    }

    private List<SaleDetail> createSaleDetails(List<SaleDetailRequest> requests) {
        return requests.stream()
                .map(this::processSingleProduct)
                .toList();
    }

    private SaleDetail processSingleProduct(SaleDetailRequest request) {
        Product product = getAndValidateProduct(request.productId());
        updateProductStock(product, request.quantity());
        return SaleDetail.createNew(product.getId(), request.quantity(), product.getPrice());
    }

    private Product getAndValidateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("El producto con ID " + productId + " no existe."));
        securityUtils.validateSameGym(product.getGymId());

        return product;
    }

    private void updateProductStock(Product product, Integer quantityToReduce) {
        product.reduceStock(quantityToReduce);
        productRepository.save(product);
    }

    private Sale persistSale(CreateSaleRequest request, Long gymId, Long adminId, List<SaleDetail> details) {
        Sale sale = Sale.createNew(request.paymentMethod(), gymId, adminId, request.memberId(), details);
        return saleRepository.save(sale);
    }

    private void registerIncomeInLedger(Sale sale, Long gymId, Long adminId) {
        RegisterTransactionCommand command = buildTransactionCommand(sale, gymId, adminId);
        registerTransactionUseCase.execute(command);
    }

    private RegisterTransactionCommand buildTransactionCommand(Sale sale, Long gymId, Long adminId) {
        return new RegisterTransactionCommand(
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                TransactionCategory.PRODUCT,
                "Venta de Kiosco #" + sale.getId(),
                gymId,
                adminId,
                null,
                sale.getId()
        );
    }
}