import { useState, useEffect } from 'react';
import { memberService } from '../services/member.service';
import { Member } from '../types/member.types';
import { authService } from '@/features/auth/services/auth.service';

export const useMembers = () => {
  const [members, setMembers] = useState<Member[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [stats, setStats] = useState({ total: 0, active: 0, inactive: 0 });
  const [currentFilter, setCurrentFilter] = useState<string | undefined>(undefined);

  const fetchMembers = async (status?: string) => {
    setIsLoading(true);
    setError(null);
    setCurrentFilter(status);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');

      // Obtenemos el listado según el filtro
      const memberList = await memberService.getAll(user.gymId, status);
      
      // Enriquecemos con suscripciones para la UI
      const enrichedMembers = await Promise.all(
        memberList.map(async (m) => {
          const sub = await memberService.getActiveSubscription(m.id);
          return { ...m, subscription: sub || undefined };
        })
      );

      // Si no hay filtro, actualizamos las estadísticas globales
      if (!status) {
        const activeCount = enrichedMembers.filter(m => m.subscription?.status === 'ACTIVE').length;
        setStats({
          total: enrichedMembers.length,
          active: activeCount,
          inactive: enrichedMembers.length - activeCount
        });
      }
      
      setMembers(enrichedMembers);
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
      setMembers(prev => prev.filter(m => m.id !== id));
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
      
      // En lugar de solo añadir, refrescamos todo para sincronizar KPIs y Suscripciones
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
