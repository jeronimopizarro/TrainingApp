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
            BigDecimal monthlyRevenue,    
            BigDecimal lastMonthRevenue,  
            String revenueGrowthPercentage, // CALCULADO: "+15.5%"
            BigDecimal weeklyRevenue,     
            BigDecimal dailyRevenue,      
            BigDecimal membershipRevenue, 
            BigDecimal productsRevenue   
    ) {}

    // Resumen de Gente
    public record AudienceSummary(
            long activeMembers,           
            long lastMonthActiveMembers,  
            long activeMembersGrowth,     // CALCULADO: "+5" o "-2"
            long newMembersThisMonth,     
            long churnedMembersThisMonth, 
            long lastMonthChurnedMembers,
            String churnTrend             // CALCULADO: "Mejoró" o "Subió"
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