import { useState, useEffect, useCallback } from "react";
import { accessService } from "../services/access.service";
import { AccessLogResponse } from "../types/access.types";

export const useAccessLogs = () => {
  const [logs, setLogs] = useState<AccessLogResponse[]>([]);
  const [stats, setStats] = useState({ successfulToday: 0, failedToday: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentFilter, setCurrentFilter] = useState<boolean | undefined>(undefined);

  const fetchLogs = useCallback(async (granted?: boolean) => {
    try {
      setLoading(true);
      setCurrentFilter(granted);
      const data = await accessService.getAccessLogs(granted);
      setLogs(data.logs);
      setStats({
        successfulToday: data.totalSuccessfulEntriesToday,
        failedToday: data.totalFailedAttemptsToday
      });
      setError(null);
    } catch (err) {
      setError("Error al cargar el historial de accesos");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  return {
    logs,
    stats,
    loading,
    error,
    currentFilter,
    refreshLogs: fetchLogs,
  };
};
