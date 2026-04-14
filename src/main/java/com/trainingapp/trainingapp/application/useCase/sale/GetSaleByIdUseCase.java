package com.trainingapp.trainingapp.application.useCase.sale;

import com.trainingapp.trainingapp.application.mapper.sale.SaleDTOMapper;
import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.exception.sale.SaleNotFoundException;
import com.trainingapp.trainingapp.domain.repository.sale.SaleRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSaleByIdUseCase {

    private final SaleRepository saleRepository;
    private final SaleDTOMapper saleDTOMapper;
    private final SecurityUtils securityUtils;

    public GetSaleByIdUseCase(SaleRepository saleRepository,
                             SaleDTOMapper saleDTOMapper,
                             SecurityUtils securityUtils) {
        this.saleRepository = saleRepository;
        this.saleDTOMapper = saleDTOMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public SaleResponse execute(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Venta no encontrada con ID: " + id));

        securityUtils.validateSameGym(sale.getGymId());

        return saleDTOMapper.toResponse(sale);
    }
}
