import api from '@/shared/services/api';
import { 
  StaffMember, 
  RegisterStaffRequest, 
  UpdateStaffRequest, 
  StaffSummaryResponse 
} from '../types/staff.types';

export const staffService = {
  /**
   * Obtiene el resumen del staff (Estadísticas + Lista filtrada).
   * UNA SOLA LLAMADA AL BACKEND.
   */
  getSummary: async (gymId: number, role?: string): Promise<StaffSummaryResponse> => {
    const response = await api.get<StaffSummaryResponse>(`/staff/summary`, {
      params: { gymId, role }
    });
    return response.data;
  },

  /**
   * Registra un nuevo miembro del staff.
   */
  registerStaff: async (request: RegisterStaffRequest): Promise<StaffMember> => {
    const endpoint = request.role === 'TRAINER' ? '/trainers' : '/receptionists';
    const response = await api.post<StaffMember>(endpoint, request);
    return { ...response.data, role: request.role };
  },

  /**
   * Actualiza un miembro del staff.
   */
  updateStaff: async (id: number, role: 'TRAINER' | 'RECEPTIONIST', request: UpdateStaffRequest): Promise<StaffMember> => {
    const endpoint = role === 'TRAINER' ? `/trainers/${id}` : `/receptionists/${id}`;
    const response = await api.put<StaffMember>(endpoint, request);
    return { ...response.data, role };
  },

  /**
   * Elimina un miembro del staff.
   */
  deleteStaff: async (id: number, role: 'TRAINER' | 'RECEPTIONIST'): Promise<void> => {
    const endpoint = role === 'TRAINER' ? `/trainers/${id}` : `/receptionists/${id}`;
    await api.delete(endpoint);
  }
};
