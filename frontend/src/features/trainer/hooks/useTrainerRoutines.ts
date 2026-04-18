import { useState, useEffect } from 'react';
import { routineService } from '../../routines/services/routine.service';
import { RoutineSummary } from '../../routines/types/routine.types';
import { authService } from '../../auth/services/auth.service';

export const useTrainerRoutines = () => {
  const [routines, setRoutines] = useState<RoutineSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRoutines = async () => {
    try {
      setIsLoading(true);
      const userData = authService.getUserData();
      if (!userData) return;
      
      const data = await routineService.getAllByTrainer(userData.userId);
      setRoutines(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar tus rutinas');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRoutines();
  }, []);

  return {
    routines,
    isLoading,
    error,
    refresh: fetchRoutines
  };
};
