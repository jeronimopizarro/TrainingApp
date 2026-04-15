import api from '@/shared/services/api';
import { StartSessionRequest, SessionResponse, LogSetRequest, SetLogResponse } from '../types/tracker.types';

export const trackerService = {
  /**
   * Inicia una sesión de entrenamiento
   */
  startSession: async (request: StartSessionRequest): Promise<SessionResponse> => {
    const { data } = await api.post<SessionResponse>('/sessions/start', request);
    return data;
  },

  /**
   * Registra una serie en una sesión activa
   */
  logSet: async (sessionId: number, request: LogSetRequest): Promise<SetLogResponse> => {
    const { data } = await api.post<SetLogResponse>(`/sessions/${sessionId}/sets`, request);
    return data;
  },

  /**
   * Finaliza una sesión de entrenamiento
   */
  finishSession: async (sessionId: number): Promise<SessionResponse> => {
    const { data } = await api.patch<SessionResponse>(`/sessions/${sessionId}/finish`);
    return data;
  }
};
