import { useState, useEffect, useCallback } from "react";
import { accessService } from "../services/access.service";
import { QrTokenResponse } from "../types/access.types";
import { authService } from "@/features/auth/services/auth.service";

export const useMemberQr = () => {
  const [qrData, setQrData] = useState<QrTokenResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchQr = useCallback(async () => {
    const userData = authService.getUserData();
    if (!userData || userData.role !== 'MEMBER') return;
    
    try {
      setLoading(true);
      const data = await accessService.generateQr(userData.userId);
      setQrData(data);
      setError(null);
    } catch (err) {
      setError("Error al generar QR");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchQr();
    // Auto-refresh QR every 55 seconds (it expires in 60s)
    const interval = setInterval(fetchQr, 55000);
    return () => clearInterval(interval);
  }, [fetchQr]);

  return { qrData, loading, error, refresh: fetchQr };
};
