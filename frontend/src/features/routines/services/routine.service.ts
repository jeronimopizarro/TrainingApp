import api from '@/shared/services/api';
import { RoutineDetail, RoutineSummary, RequestRoutineMessage, CreatePersonalRoutineRequest, RoutineRequestSummary, AssignRoutineRequest, DuplicateRoutineRequest } from '../types/routine.types';

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
   * Obtiene todas las rutinas creadas por un entrenador
   */
  getAllByTrainer: async (trainerId: number): Promise<RoutineSummary[]> => {
    const { data } = await api.get<RoutineSummary[]>(`/routines?trainerId=${trainerId}`);
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
   * Obtiene solicitudes de rutina pendientes para el gimnasio del trainer
   */
  getPendingRequests: async (): Promise<RoutineRequestSummary[]> => {
    const { data } = await api.get<RoutineRequestSummary[]>('/routines/requests/pending');
    return data;
  },

  /**
   * El trainer toma una solicitud de rutina
   */
  takeRequest: async (requestId: number): Promise<void> => {
    await api.post(`/routines/requests/${requestId}/take`);
  },

  /**
   * Asigna una rutina a un miembro (desde una solicitud o manual)
   */
  assign: async (request: AssignRoutineRequest): Promise<{id: number, message: string}> => {
    const { data } = await api.post<{id: number, message: string}>('/routines/assign', request);
    return data;
  },

  /**
   * Duplica una rutina base para un miembro
   */
  duplicate: async (routineId: number, request: DuplicateRoutineRequest): Promise<{id: number, message: string}> => {
    const { data } = await api.post<{id: number, message: string}>(`/routines/${routineId}/duplicate`, request);
    return data;
  },

  /**
   * Crea una rutina personal (self-service)
   */
  createPersonal: async (request: CreatePersonalRoutineRequest): Promise<{id: number, message: string}> => {
    const { data } = await api.post<{id: number, message: string}>('/routines/personal', request);
    return data;
  },

  /**
   * Obtiene las rutinas base (plantillas) del gimnasio
   */
  getBaseRoutines: async (): Promise<RoutineSummary[]> => {
    const { data } = await api.get<RoutineSummary[]>('/routines/bases');
    return data;
  },

  /**
   * Crea una nueva rutina base (plantilla)
   */
  createBase: async (request: any): Promise<{id: number, message: string}> => {
    const { data } = await api.post<{id: number, message: string}>('/routines/bases', request);
    return data;
  },

  /**
   * Actualiza una rutina existente
   */
  update: async (id: number, request: any): Promise<void> => {
    await api.put(`/routines/${id}`, request);
  },

  /**
   * Elimina una rutina
   */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/routines/${id}`);
  }
};
