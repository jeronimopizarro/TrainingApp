import { useState, useEffect, useCallback } from 'react';
import { exerciseService } from '../services/exercise.service';
import { Exercise, MuscleGroup, CreateExerciseRequest, UpdateExerciseRequest } from '../types/exercise.types';

export const useExercises = () => {
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [muscleGroups, setMuscleGroups] = useState<MuscleGroup[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchMuscleGroups = useCallback(async () => {
    try {
      const data = await exerciseService.getMuscleGroups();
      setMuscleGroups(data);
    } catch (err) {
      console.error('Error fetching muscle groups', err);
    }
  }, []);

  const fetchExercises = useCallback(async (muscleGroupId?: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await exerciseService.getAll(muscleGroupId);
      setExercises(data);
    } catch (err: any) {
      setError('Error al cargar la biblioteca de ejercicios.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMuscleGroups();
    fetchExercises();
  }, [fetchExercises, fetchMuscleGroups]);

  const createExercise = async (request: CreateExerciseRequest) => {
    setIsLoading(true);
    try {
      // Forzamos isBase a false ya que un GYM_ADMIN no puede crear ejercicios base
      await exerciseService.create({ ...request, isBase: false });
      await fetchExercises();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al crear el ejercicio.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const updateExercise = async (id: number, request: UpdateExerciseRequest) => {
    setIsLoading(true);
    try {
      await exerciseService.update(id, request);
      await fetchExercises();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al actualizar el ejercicio.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const deleteExercise = async (id: number) => {
    try {
      await exerciseService.delete(id);
      await fetchExercises();
      return true;
    } catch (err) {
      setError('No se pudo eliminar el ejercicio.');
      return false;
    }
  };

  return {
    exercises,
    muscleGroups,
    isLoading,
    error,
    refresh: fetchExercises,
    createExercise,
    updateExercise,
    deleteExercise
  };
};
