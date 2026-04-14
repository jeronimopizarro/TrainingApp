export enum PaymentMethod {
  CASH = "CASH",
  CARD = "CARD",
  TRANSFER = "TRANSFER",
  VIRTUAL_WALLET = "VIRTUAL_WALLET",
}

export interface MembershipPlan {
  id: number;
  name: string;
  description: string;
  price: number;
  durationMonths: number;
  gymId: number;
  active: boolean;
}

export interface CreateMembershipPlanRequest {
  name: string;
  description: string;
  price: number;
  durationMonths: number;
  gymId: number;
}

export interface UpdateMembershipPlanRequest {
  name: string;
  description: string;
  price: number;
  durationMonths: number;
}

export interface CreateSubscriptionRequest {
  memberId: number;
  planId: number;
  startDate: string;
  paymentMethod: PaymentMethod;
}

export interface SubscriptionResponse {
  id: number;
  memberId: number;
  planId: number;
  planName: string;
  startDate: string;
  endDate: string;
  status: string;
  isActive: boolean;
}
