import { useState, useEffect } from 'react';
import { routineService } from '../../routines/services/routine.service';
import { RoutineSummary } from '../../routines/types/routine.types';

export const useBaseRoutines = () => {
  const [routines, setRoutines] = useState<RoutineSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchBaseRoutines = async () => {
    try {
      setIsLoading(true);
      const data = await routineService.getBaseRoutines();
      setRoutines(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar las rutinas base');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const duplicateRoutine = async (routineId: number, targetMemberId: number, newName: string) => {
    try {
      await routineService.duplicate(routineId, { targetMemberId, newRoutineName: newName });
      return true;
    } catch (err) {
      console.error('Error al duplicar la rutina:', err);
      return false;
    }
  };

  useEffect(() => {
    fetchBaseRoutines();
  }, []);

  return {
    routines,
    isLoading,
    error,
    refresh: fetchBaseRoutines,
    duplicateRoutine
  };
};
