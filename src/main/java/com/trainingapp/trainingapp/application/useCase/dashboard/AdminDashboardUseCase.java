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

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        BigDecimal currentRevenue = transactionRepository.sumRevenueByDateRange(gymId, startOfMonth, endOfMonth);
        BigDecimal lastRevenue = transactionRepository.sumRevenueByDateRange(gymId, startOfLastMonth, endOfLastMonth);
        
        // Calcular Porcentaje de Crecimiento
        String growth = "0%";
        if (lastRevenue != null && lastRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = currentRevenue.subtract(lastRevenue);
            BigDecimal percentage = diff.divide(lastRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            growth = (percentage.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + 
                     percentage.setScale(1, RoundingMode.HALF_UP).toString() + "%";
        } else if (currentRevenue != null && currentRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growth = "+100%";
        }

        return new FinancialSummary(
                currentRevenue,
                lastRevenue,
                growth,
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

        long currentActive = subscriptionRepository.countActiveMembersByGymId(gymId);
        long lastActive = subscriptionRepository.countActiveMembersByGymId(gymId); // Simulación mes pasado
        long currentChurn = subscriptionRepository.countChurnedMembersByGymIdAndDateRange(gymId, startOfMonth, endOfMonth);
        long lastChurn = subscriptionRepository.countChurnedMembersByGymIdAndDateRange(gymId, startOfLastMonth, endOfLastMonth);

        return new AudienceSummary(
                currentActive,
                lastActive,
                currentActive - lastActive,
                subscriptionRepository.countNewMembersByGymIdAndDateRange(gymId, startOfMonth, endOfMonth),
                currentChurn,
                lastChurn,
                lastChurn >= currentChurn ? "Mejoró" : "Subió"
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
