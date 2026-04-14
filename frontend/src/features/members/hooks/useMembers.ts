import { useState, useEffect } from 'react';
import { memberService } from '../services/member.service';
import { MemberListItem, MemberStats } from '../types/member.types';
import { authService } from '@/features/auth/services/auth.service';

export const useMembers = () => {
  const [members, setMembers] = useState<MemberListItem[]>([]);
  const [stats, setStats] = useState<MemberStats>({ total: 0, active: 0, inactive: 0 });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentFilter, setCurrentFilter] = useState<string | undefined>(undefined);

  const fetchMembers = async (status?: string) => {
    setIsLoading(true);
    setError(null);
    setCurrentFilter(status);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');

      // Obtenemos el resumen consolidado (UNA SOLA LLAMADA API)
      const data = await memberService.getSummary(user.gymId, status);
      
      setMembers(data.members);
      setStats(data.stats);
    } catch (err: any) {
      setError('Error al cargar socios: ' + (err.message || 'Verifica tu conexión.'));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
  }, []);

  const deleteMember = async (id: number) => {
    try {
      await memberService.delete(id);
      await fetchMembers(currentFilter);
      return true;
    } catch (err) {
      setError('No se pudo eliminar al socio.');
      return false;
    }
  };

  const registerMember = async (data: any) => {
    setIsLoading(true);
    try {
      const user = authService.getUserData();
      await memberService.register({
        ...data,
        gymId: user?.gymId,
        password: data.dni
      });
      await fetchMembers(currentFilter);
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrar socio.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    members,
    stats,
    isLoading,
    error,
    refresh: fetchMembers,
    deleteMember,
    registerMember
  };
};
