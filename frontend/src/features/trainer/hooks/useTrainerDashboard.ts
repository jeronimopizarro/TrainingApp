import { useState, useEffect } from 'react';
import { routineService } from '../../routines/services/routine.service';
import { RoutineRequestSummary } from '../../routines/types/routine.types';

export const useTrainerDashboard = () => {
  const [requests, setRequests] = useState<RoutineRequestSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRequests = async () => {
    try {
      setIsLoading(true);
      const data = await routineService.getPendingRequests();
      setRequests(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar las solicitudes de rutina');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const takeRequest = async (requestId: number) => {
    try {
      await routineService.takeRequest(requestId);
      // Refrescar la lista o redirigir al creador de rutinas
      await fetchRequests();
      return true;
    } catch (err) {
      console.error('Error al tomar la solicitud:', err);
      return false;
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  return {
    requests,
    isLoading,
    error,
    refreshRequests: fetchRequests,
    takeRequest
  };
};
