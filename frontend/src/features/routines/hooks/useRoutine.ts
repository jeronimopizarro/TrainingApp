import { useState, useEffect, useCallback } from 'react';
import { routineService } from '../services/routine.service';
import { RoutineDetail, RoutineSummary } from '../types/routine.types';

export const useRoutine = (memberId?: number, routineId?: number) => {
  const [activeRoutine, setActiveRoutine] = useState<RoutineSummary | null>(null);
  const [detail, setDetail] = useState<RoutineDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchActive = useCallback(async () => {
    if (!memberId) return;
    try {
      setLoading(true);
      const data = await routineService.getActive(memberId);
      setActiveRoutine(data);
      if (data) {
        const fullDetail = await routineService.getById(data.id);
        setDetail(fullDetail);
      }
      setError(null);
    } catch (err) {
      setError('No tienes una rutina activa asignada.');
    } finally {
      setLoading(false);
    }
  }, [memberId]);

  const fetchById = useCallback(async (id: number) => {
    try {
      setLoading(true);
      const data = await routineService.getById(id);
      setDetail(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar el detalle de la rutina.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (routineId) {
      fetchById(routineId);
    } else if (memberId) {
      fetchActive();
    }
  }, [memberId, routineId, fetchActive, fetchById]);

  return { activeRoutine, detail, loading, error, refresh: fetchActive };
};
