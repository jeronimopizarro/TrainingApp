import { useState, useCallback, useEffect } from 'react';
import { trackerService } from '../services/tracker.service';
import { MemberProgressSummaryResponse, ExerciseProgressResponse } from '../types/tracker.types';

export const useProgress = (exerciseId?: number) => {
  const [summary, setSummary] = useState<MemberProgressSummaryResponse | null>(null);
  const [exerciseProgress, setExerciseProgress] = useState<ExerciseProgressResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchSummary = useCallback(async () => {
    try {
      setLoading(true);
      const data = await trackerService.getProgressSummary();
      setSummary(data);
      setError(null);
    } catch (err) {
      setError('Error al obtener el resumen de progreso');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchExerciseProgress = useCallback(async (id: number, months: number = 6) => {
    try {
      setLoading(true);
      const data = await trackerService.getExerciseProgress(id, months);
      setExerciseProgress(data);
      setError(null);
    } catch (err) {
      setError('Error al obtener el progreso del ejercicio');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (exerciseId) {
      fetchExerciseProgress(exerciseId);
    } else {
      fetchSummary();
    }
  }, [exerciseId, fetchSummary, fetchExerciseProgress]);

  return { summary, exerciseProgress, loading, error, refreshSummary: fetchSummary, fetchExerciseProgress };
};
