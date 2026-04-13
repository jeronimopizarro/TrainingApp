import { useState, useEffect } from 'react';
import { dashboardService } from '../services/dashboard.service';
import { AdminDashboardData } from '../types/dashboard.types';

export const useDashboard = () => {
  const [data, setData] = useState<AdminDashboardData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchStats = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const stats = await dashboardService.getAdminStats();
      setData(stats);
    } catch (err: any) {
      setError('No se pudieron cargar las métricas. Verifica tu conexión.');
    } finally {
      setIsLoading(false);
    }
  };

  // Cargar datos automáticamente al montar el componente
  useEffect(() => {
    fetchStats();
  }, []);

  return {
    data,
    isLoading,
    error,
    refresh: fetchStats
  };
};
