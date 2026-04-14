export type SubscriptionStatus = 'ACTIVE' | 'INACTIVE' | 'EXPIRED' | 'CANCELLED' | 'NONE';

export interface MemberListItem {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  dni: string;
  planName: string;
  subscriptionStatus: SubscriptionStatus;
  endDate: string | null;
}

export interface MemberStats {
  total: number;
  active: number;
  inactive: number;
}

export interface MemberSummaryResponse {
  stats: MemberStats;
  members: MemberListItem[];
}

export interface Member {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  dni: string;
  active: boolean;
  gymId: number;
  birthDate: string;
  primaryGoal?: string;
}

export interface RegisterMemberRequest {
  firstName: string;
  lastName: string;
  email: string;
  dni: string;
  gymId: number;
  birthDate: string;
  primaryGoal?: string;
}

export interface UpdateMemberRequest {
  firstName?: string;
  lastName?: string;
  dni?: string;
  birthDate?: string;
  primaryGoal?: string;
}
