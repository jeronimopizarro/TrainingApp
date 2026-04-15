import { useState, useEffect, useCallback } from 'react';
import { dashboardService } from '../services/dashboard.service';
import { MemberDashboardData } from '../types/dashboard.types';

export const useMemberDashboard = () => {
  const [data, setData] = useState<MemberDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const dashboardData = await dashboardService.getMemberStats();
      setData(dashboardData);
      setError(null);
    } catch (err) {
      setError('Error al cargar los datos del dashboard');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refresh: fetchData };
};
