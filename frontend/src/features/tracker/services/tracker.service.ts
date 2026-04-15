import api from '@/shared/services/api';
import { 
  StartSessionRequest, 
  SessionResponse, 
  LogSetRequest, 
  SetLogResponse,
  MemberProgressSummaryResponse,
  ExerciseProgressResponse
} from '../types/tracker.types';

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
  },

  /**
   * Obtiene el resumen de progreso del miembro
   */
  getProgressSummary: async (): Promise<MemberProgressSummaryResponse> => {
    const { data } = await api.get<MemberProgressSummaryResponse>('/progress/summary');
    return data;
  },

  /**
   * Obtiene el progreso detallado de un ejercicio
   */
  getExerciseProgress: async (exerciseId: number, monthsBack: number = 6): Promise<ExerciseProgressResponse> => {
    const { data } = await api.get<ExerciseProgressResponse>(`/progress/exercise/${exerciseId}`, {
      params: { monthsBack }
    });
    return data;
  }
};
