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
