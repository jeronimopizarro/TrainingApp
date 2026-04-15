import { useState, useCallback } from 'react';
import { trackerService } from '../services/tracker.service';
import { SessionResponse, LogSetRequest } from '../types/tracker.types';

export const useWorkoutTracker = () => {
  const [session, setSession] = useState<SessionResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const startWorkout = useCallback(async (routineDayId: number) => {
    try {
      setLoading(true);
      const data = await trackerService.startSession({ routineDayId });
      setSession(data);
      setError(null);
      return data;
    } catch (err) {
      setError('Error al iniciar la sesión de entrenamiento');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const logSet = useCallback(async (exerciseId: number, reps: number, weight: number, rir: number) => {
    if (!session) return;
    try {
      const request: LogSetRequest = { exerciseId, reps, weight, rir };
      await trackerService.logSet(session.id, request);
    } catch (err) {
      console.error('Error logging set', err);
      throw err;
    }
  }, [session]);

  const finishWorkout = useCallback(async () => {
    if (!session) return;
    try {
      setLoading(true);
      await trackerService.finishSession(session.id);
      setSession(null);
      setError(null);
    } catch (err) {
      setError('Error al finalizar la sesión');
      throw err;
    } finally {
      setLoading(false);
    }
  }, [session]);

  return { session, loading, error, startWorkout, logSet, finishWorkout };
};
