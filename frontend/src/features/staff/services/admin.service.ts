import api from '@/shared/services/api';

export interface AdminResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  dni: string;
  role: string;
  gymId: number;
  active: boolean;
}

export const adminService = {
  getById: async (id: number): Promise<AdminResponse> => {
    const response = await api.get<AdminResponse>(`/admins/${id}`);
    return response.data;
  }
};
