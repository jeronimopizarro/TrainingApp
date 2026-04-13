package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale.SaleJpaRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.subscription.SubscriptionJpaRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction.TransactionJpaRepository;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminDashboardUseCase {

    private final TransactionJpaRepository transactionRepository;
    private final SubscriptionJpaRepository subscriptionRepository;
    private final SaleJpaRepository saleRepository;
    private final SecurityUtils securityUtils;

    public AdminDashboardUseCase(TransactionJpaRepository transactionRepository,
                                 SubscriptionJpaRepository subscriptionRepository,
                                 SaleJpaRepository saleRepository,
                                 SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.saleRepository = saleRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse execute() {
        Long gymId = securityUtils.getCurrentUserGymId();
        LocalDate today = LocalDate.now();

        FinancialSummary financialSummary = buildFinancialSummary(gymId, today);
        AudienceSummary audienceSummary = buildAudienceSummary(gymId, today);
        List<TopProductDTO> topProducts = getTopProducts(gymId);
        List<ExpiringMembershipDTO> expiringMemberships = getExpiringMemberships(gymId, today);

        return new AdminDashboardResponse(financialSummary, audienceSummary, topProducts, expiringMemberships);
    }

    private FinancialSummary buildFinancialSummary(Long gymId, LocalDate today) {
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay();
        
        // Mes Actual
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
        
        // Mes Pasado
        LocalDate lastMonthDate = today.minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonthDate.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonthDate.withDayOfMonth(lastMonthDate.lengthOfMonth()).atTime(LocalTime.MAX);

        return new FinancialSummary(
                transactionRepository.sumRevenueByDateRange(gymId, startOfMonth, endOfMonth),
                transactionRepository.sumRevenueByDateRange(gymId, startOfLastMonth, endOfLastMonth),
                transactionRepository.sumRevenueByDateRange(gymId, startOfWeek, endOfDay),
                transactionRepository.sumRevenueByDateRange(gymId, startOfDay, endOfDay),
                transactionRepository.sumRevenueByCategoryAndDateRange(gymId, TransactionCategory.MEMBERSHIP, startOfMonth, endOfMonth),
                transactionRepository.sumRevenueByCategoryAndDateRange(gymId, TransactionCategory.PRODUCT, startOfMonth, endOfMonth)
        );
    }

    private AudienceSummary buildAudienceSummary(Long gymId, LocalDate today) {
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        
        LocalDate lastMonthDate = today.minusMonths(1);
        LocalDate startOfLastMonth = lastMonthDate.withDayOfMonth(1);
        LocalDate endOfLastMonth = lastMonthDate.withDayOfMonth(lastMonthDate.lengthOfMonth());

        return new AudienceSummary(
                subscriptionRepository.countActiveMembersByGymId(gymId),
                subscriptionRepository.countActiveMembersByGymId(gymId), // Por ahora usamos el actual como base
                subscriptionRepository.countNewMembersByGymIdAndDateRange(gymId, startOfMonth, endOfMonth),
                subscriptionRepository.countChurnedMembersByGymIdAndDateRange(gymId, startOfMonth, endOfMonth),
                subscriptionRepository.countChurnedMembersByGymIdAndDateRange(gymId, startOfLastMonth, endOfLastMonth)
        );
    }

    private List<TopProductDTO> getTopProducts(Long gymId) {
        return saleRepository.findTopProductsByGym(gymId, PageRequest.of(0, 3));
    }

    private List<ExpiringMembershipDTO> getExpiringMemberships(Long gymId, LocalDate today) {
        LocalDate limitDateForExpiring = today.plusDays(3);
        return subscriptionRepository.findExpiringMemberships(gymId, today, limitDateForExpiring);
    }
}
