package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale.SaleJpaRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.subscription.SubscriptionJpaRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.transaction.TransactionJpaRepository;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse.ExpiringMembershipDTO;
import com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse.TopProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardUseCaseTest {

    @Mock private TransactionJpaRepository transactionRepository;
    @Mock private SubscriptionJpaRepository subscriptionRepository;
    @Mock private SaleJpaRepository saleRepository;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private AdminDashboardUseCase useCase;

    @Test
    @DisplayName("Debería compilar y retornar todas las métricas del Admin Dashboard usando JpaRepositories")
    void shouldReturnAdminDashboardMetrics() {
        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);

        // Mock Financial Summary
        when(transactionRepository.sumRevenueByDateRange(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("5000.00")); // Mensual
        when(transactionRepository.sumRevenueByCategoryAndDateRange(eq(10L), eq(TransactionCategory.MEMBERSHIP), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("4000.00")); // Cuotas
        when(transactionRepository.sumRevenueByCategoryAndDateRange(eq(10L), eq(TransactionCategory.PRODUCT), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("1000.00")); // Productos

        // Mock Audience Summary
        when(subscriptionRepository.countActiveMembersByGymId(10L)).thenReturn(150L);
        when(subscriptionRepository.countNewMembersByGymIdAndDateRange(eq(10L), any(LocalDate.class), any(LocalDate.class))).thenReturn(20L);
        when(subscriptionRepository.countChurnedMembersByGymIdAndDateRange(eq(10L), any(LocalDate.class), any(LocalDate.class))).thenReturn(5L);

        // Mock Kiosco y Retención
        List<TopProductDTO> topProducts = List.of(new TopProductDTO(1L, "Whey Protein", 50L));
        when(saleRepository.findTopProductsByGym(eq(10L), any(PageRequest.class))).thenReturn(topProducts);

        List<ExpiringMembershipDTO> expiring = List.of(new ExpiringMembershipDTO(100L, "Juan", "Perez", LocalDate.now().plusDays(2)));
        when(subscriptionRepository.findExpiringMemberships(eq(10L), any(LocalDate.class), any(LocalDate.class))).thenReturn(expiring);

        AdminDashboardResponse response = useCase.execute();

        // Verificamos Finanzas
        assertEquals(new BigDecimal("5000.00"), response.financialSummary().monthlyRevenue());
        assertEquals(new BigDecimal("4000.00"), response.financialSummary().membershipRevenue());
        assertEquals(new BigDecimal("1000.00"), response.financialSummary().productsRevenue());

        // Verificamos Audiencia
        assertEquals(150L, response.audienceSummary().activeMembers());
        assertEquals(20L, response.audienceSummary().newMembersThisMonth());
        assertEquals(5L, response.audienceSummary().churnedMembersThisMonth());

        // Verificamos Kiosco y Expiring
        assertEquals(1, response.topProducts().size());
        assertEquals("Whey Protein", response.topProducts().get(0).name());
        assertEquals(1, response.expiringMemberships().size());
        assertEquals("Juan", response.expiringMemberships().get(0).memberName());
    }
}