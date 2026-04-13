export interface FinancialSummary {
  monthlyRevenue: number;
  lastMonthRevenue: number; // NUEVO
  weeklyRevenue: number;
  dailyRevenue: number;
  membershipRevenue: number;
  productsRevenue: number;
}

export interface AudienceSummary {
  activeMembers: number;
  lastMonthActiveMembers: number; // NUEVO
  newMembersThisMonth: number;
  churnedMembersThisMonth: number;
  lastMonthChurnedMembers: number; // NUEVO
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
