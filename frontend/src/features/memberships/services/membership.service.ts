import api from '@/shared/services/api';
import { MembershipPlan, CreateMembershipPlanRequest, UpdateMembershipPlanRequest } from '../types/membership.types';

export const membershipService = {
  getAllByGym: async (gymId: number): Promise<MembershipPlan[]> => {
    const response = await api.get<MembershipPlan[]>(`/membership-plans`, {
      params: { gymId }
    });
    return response.data;
  },

  create: async (request: CreateMembershipPlanRequest): Promise<MembershipPlan> => {
    const response = await api.post<MembershipPlan>(`/membership-plans`, request);
    return response.data;
  },

  update: async (id: number, request: UpdateMembershipPlanRequest): Promise<MembershipPlan> => {
    const response = await api.put<MembershipPlan>(`/membership-plans/${id}`, request);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/membership-plans/${id}`);
  }
};
