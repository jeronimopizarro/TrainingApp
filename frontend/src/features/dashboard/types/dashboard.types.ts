export interface FinancialSummary {
  monthlyRevenue: number;
  lastMonthRevenue: number;
  revenueGrowthPercentage: string; // NUEVO: "+15.5%"
  weeklyRevenue: number;
  dailyRevenue: number;
  membershipRevenue: number;
  productsRevenue: number;
}

export interface AudienceSummary {
  activeMembers: number;
  lastMonthActiveMembers: number;
  activeMembersGrowth: number;
  newMembersThisMonth: number;
  churnedMembersThisMonth: number;
  lastMonthChurnedMembers: number;
  churnTrend: string;
}

export interface TopProduct {
  productId: number;
  name: string;
  totalQuantitySold: number;
}

export interface ExpiringMembership {
  memberId: number;
  memberName: string;
  memberLastName: string;
  expirationDate: string;
}

export interface AdminDashboardData {
  financialSummary: FinancialSummary;
  audienceSummary: AudienceSummary;
  topProducts: TopProduct[];
  expiringMemberships: ExpiringMembership[];
}
