export type SubscriptionStatus = 'ACTIVE' | 'INACTIVE' | 'EXPIRED' | 'CANCELLED';

export interface MemberSubscription {
  status: SubscriptionStatus;
  planName: string;
  endDate: string;
  remainingDays: number;
  isActive: boolean;
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
  subscription?: MemberSubscription;
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
