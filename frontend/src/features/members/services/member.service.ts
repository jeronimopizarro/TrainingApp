import api from '@/shared/services/api';
import { Member, RegisterMemberRequest, UpdateMemberRequest, MemberSubscription, MemberSummaryResponse } from '../types/member.types';

export const memberService = {
  /**
   * Obtiene el resumen consolidado de socios (Lista + KPIs)
   * Resuelve el problema N+1 delegando al Backend
   */
  getSummary: async (gymId: number, status?: string): Promise<MemberSummaryResponse> => {
    const url = status ? `/members/summary?gymId=${gymId}&status=${status}` : `/members/summary?gymId=${gymId}`;
    const { data } = await api.get<MemberSummaryResponse>(url);
    return data;
  },

  /**
   * Obtener todos los socios por Gym ID con filtro opcional de estado
   */
  getAll: async (gymId: number, status?: string): Promise<Member[]> => {
    const url = status ? `/members?gymId=${gymId}&status=${status}` : `/members?gymId=${gymId}`;
    const { data } = await api.get<Member[]>(url);
    return data;
  },

  /**
   * Obtener un socio por su ID
   */
  getById: async (id: number): Promise<Member> => {
    const { data } = await api.get<Member>(`/members/${id}`);
    return data;
  },

  /**
   * Obtener la suscripción activa de un socio
   */
  getActiveSubscription: async (memberId: number): Promise<MemberSubscription | null> => {
    try {
      const { data } = await api.get<MemberSubscription>(`/subscriptions/active?memberId=${memberId}`);
      return data;
    } catch (err) {
      // Si no tiene suscripción activa, el backend suele devolver 404
      return null;
    }
  },

  /**
   * Registrar un nuevo socio
   */
  register: async (request: RegisterMemberRequest): Promise<Member> => {
    const { data } = await api.post<Member>('/members', request);
    return data;
  },

  /**
   * Actualizar datos de un socio
   */
  update: async (id: number, request: UpdateMemberRequest): Promise<Member> => {
    const { data } = await api.put<Member>(`/members/${id}`, request);
    return data;
  },

  /**
   * Eliminar un socio
   */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/members/${id}`);
  }
};
