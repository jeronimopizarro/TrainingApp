import api from '@/shared/services/api';
import { RoutineDetail, RoutineSummary, RequestRoutineMessage, CreatePersonalRoutineRequest } from '../types/routine.types';

export const routineService = {
  /**
   * Obtiene una rutina por su ID
   */
  getById: async (id: number): Promise<RoutineDetail> => {
    const { data } = await api.get<RoutineDetail>(`/routines/${id}`);
    return data;
  },

  /**
   * Obtiene todas las rutinas de un socio
   */
  getAllByMember: async (memberId: number): Promise<RoutineSummary[]> => {
    const { data } = await api.get<RoutineSummary[]>(`/routines?memberId=${memberId}`);
    return data;
  },

  /**
   * Obtiene la rutina activa de un socio
   */
  getActive: async (memberId: number): Promise<RoutineSummary> => {
    const { data } = await api.get<RoutineSummary>(`/routines/active?memberId=${memberId}`);
    return data;
  },

  /**
   * Solicita una rutina al staff
   */
  request: async (request: RequestRoutineMessage): Promise<void> => {
    await api.post('/routines/request', request);
  },

  /**
   * Crea una rutina personal (self-service)
   */
  createPersonal: async (request: CreatePersonalRoutineRequest): Promise<{id: number, message: string}> => {
    const { data } = await api.post<{id: number, message: string}>('/routines/personal', request);
    return data;
  }
};
