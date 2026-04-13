package com.trainingapp.trainingapp.web.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AdminDashboardResponse(
        FinancialSummary financialSummary,
        AudienceSummary audienceSummary,
        List<TopProductDTO> topProducts,
        List<ExpiringMembershipDTO> expiringMemberships
) {
    // Resumen de Plata
    public record FinancialSummary(
            BigDecimal monthlyRevenue,    // Recaudado en el mes actual
            BigDecimal lastMonthRevenue,  // Recaudado el mes pasado (NUEVO)
            BigDecimal weeklyRevenue,     // Recaudado esta semana
            BigDecimal dailyRevenue,      // Recaudado HOY
            BigDecimal membershipRevenue, // Cuota de socios (del mes)
            BigDecimal productsRevenue   // Ventas de kiosco (del mes)
    ) {}

    // Resumen de Gente
    public record AudienceSummary(
            long activeMembers,           // Socios actuales
            long lastMonthActiveMembers,  // Socios el mes pasado (NUEVO)
            long newMembersThisMonth,     // Altas del mes
            long churnedMembersThisMonth, // Bajas del mes
            long lastMonthChurnedMembers  // Bajas el mes pasado (NUEVO)
    ) {}

    // Kiosco
    public record TopProductDTO(
            Long productId,
            String name,
            long totalQuantitySold
    ) {}

    // Retención y Cobranzas
    public record ExpiringMembershipDTO(
            Long memberId,
            String memberName,
            String memberLastName,
            LocalDate expirationDate
    ) {}
}