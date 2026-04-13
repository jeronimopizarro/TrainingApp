import api from '@/shared/services/api';
import { AdminDashboardData } from '../types/dashboard.types';

export const dashboardService = {
  getAdminStats: async (): Promise<AdminDashboardData> => {
    const { data } = await api.get<AdminDashboardData>('/dashboard/admin');
    return data;
  }
};
