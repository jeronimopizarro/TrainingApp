import api from '@/shared/services/api';
import { 
  Exercise, 
  MuscleGroup, 
  CreateExerciseRequest, 
  UpdateExerciseRequest 
} from '../types/exercise.types';

export const exerciseService = {
  getAll: async (muscleGroupId?: number): Promise<Exercise[]> => {
    const response = await api.get<Exercise[]>(`/exercises`, {
      params: { muscleGroupId }
    });
    return response.data;
  },

  getById: async (id: number): Promise<Exercise> => {
    const response = await api.get<Exercise>(`/exercises/${id}`);
    return response.data;
  },

  create: async (request: CreateExerciseRequest): Promise<Exercise> => {
    // Limpiamos el objeto para asegurar que solo enviamos lo que el DTO espera
    const cleanRequest = {
      name: request.name,
      description: request.description,
      imageUrl: request.imageUrl || '',
      videoUrl: request.videoUrl || '',
      isBase: false, // GYM_ADMIN siempre crea ejercicios de gimnasio, nunca base
      muscleGroups: request.muscleGroups.map(mg => ({
        muscleGroupId: mg.muscleGroupId,
        isPrimary: mg.isPrimary
      }))
    };
    const response = await api.post<Exercise>(`/exercises`, cleanRequest);
    return response.data;
  },

  update: async (id: number, request: UpdateExerciseRequest): Promise<Exercise> => {
    const response = await api.put<Exercise>(`/exercises/${id}`, request);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/exercises/${id}`);
  },

  // Muscle Groups
  getMuscleGroups: async (): Promise<MuscleGroup[]> => {
    const response = await api.get<MuscleGroup[]>(`/muscle-groups`);
    return response.data;
  }
};
