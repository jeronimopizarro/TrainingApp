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
            BigDecimal weeklyRevenue,     // Recaudado esta semana
            BigDecimal dailyRevenue,      // Recaudado HOY
            BigDecimal membershipRevenue, // Cuota de socios (del mes)
            BigDecimal productsRevenue   // Ventas de kiosco (del mes)
    ) {}

    // Resumen de Gente
    public record AudienceSummary(
            long activeMembers,     // Socios con cuota al día
            long newMembersThisMonth, // Altas del mes
            long churnedMembersThisMonth // Bajas del mes (venció y no renovó)
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