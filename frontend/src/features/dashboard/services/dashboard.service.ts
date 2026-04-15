import api from '@/shared/services/api';
import { AdminDashboardData, MemberDashboardData } from '../types/dashboard.types';

export const dashboardService = {
  getAdminStats: async (): Promise<AdminDashboardData> => {
    const { data } = await api.get<AdminDashboardData>('/dashboard/admin');
    return data;
  },

  getMemberStats: async (): Promise<MemberDashboardData> => {
    const { data } = await api.get<MemberDashboardData>('/dashboard/member');
    return data;
  }
};
