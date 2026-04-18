import { useState, useCallback, useEffect } from 'react';
import { trackerService } from '../services/tracker.service';
import { MemberProgressSummaryResponse, ExerciseProgressResponse } from '../types/tracker.types';

export const useProgress = (exerciseId?: number, memberId?: number) => {
  const [summary, setSummary] = useState<MemberProgressSummaryResponse | null>(null);
  const [exerciseProgress, setExerciseProgress] = useState<ExerciseProgressResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchSummary = useCallback(async () => {
    try {
      setLoading(true);
      const data = await trackerService.getProgressSummary(memberId);
      setSummary(data);
      setError(null);
    } catch (err) {
      setError('Error al obtener el resumen de progreso');
    } finally {
      setLoading(false);
    }
  }, [memberId]);

  const fetchExerciseProgress = useCallback(async (id: number, months: number = 6) => {
    try {
      setLoading(true);
      const data = await trackerService.getExerciseProgress(id, memberId, months);
      setExerciseProgress(data);
      setError(null);
    } catch (err) {
      setError('Error al obtener el progreso del ejercicio');
    } finally {
      setLoading(false);
    }
  }, [memberId]);

  useEffect(() => {
    if (exerciseId) {
      fetchExerciseProgress(exerciseId);
    } else {
      fetchSummary();
    }
  }, [exerciseId, fetchSummary, fetchExerciseProgress]);

  return { summary, exerciseProgress, loading, error, refreshSummary: fetchSummary, fetchExerciseProgress };
};
