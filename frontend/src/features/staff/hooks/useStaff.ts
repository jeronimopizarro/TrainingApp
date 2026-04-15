import { useState, useEffect, useCallback } from 'react';
import { staffService } from '../services/staff.service';
import { StaffMember, RegisterStaffRequest, UpdateStaffRequest, StaffStats } from '../types/staff.types';
import { authService } from '@/features/auth/services/auth.service';

export const useStaff = () => {
  const [staff, setStaff] = useState<StaffMember[]>([]);
  const [stats, setStats] = useState<StaffStats>({ total: 0, trainers: 0, receptionists: 0 });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentFilter, setCurrentFilter] = useState<string | undefined>(undefined);

  const fetchStaff = useCallback(async (role?: string) => {
    setIsLoading(true);
    setError(null);
    setCurrentFilter(role);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');

      const data = await staffService.getSummary(user.gymId, role);
      setStaff(data.staffMembers);
      setStats(data.stats);
    } catch (err: any) {
      setError('Error al cargar personal: ' + (err.message || 'Verifica tu conexión.'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchStaff();
  }, [fetchStaff]);

  const registerStaff = async (data: RegisterStaffRequest) => {
    setIsLoading(true);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');

      await staffService.registerStaff({
        ...data,
        gymId: user.gymId,
        password: data.dni // Contraseña por defecto es el DNI
      });
      await fetchStaff(currentFilter);
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrar personal.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const updateStaff = async (id: number, role: 'TRAINER' | 'RECEPTIONIST', data: UpdateStaffRequest) => {
    setIsLoading(true);
    try {
      await staffService.updateStaff(id, role, data);
      await fetchStaff(currentFilter);
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al actualizar personal.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const deleteStaff = async (id: number, role: 'TRAINER' | 'RECEPTIONIST') => {
    try {
      await staffService.deleteStaff(id, role);
      await fetchStaff(currentFilter);
      return true;
    } catch (err) {
      setError('No se pudo eliminar al miembro del personal.');
      return false;
    }
  };

  return {
    staff,
    stats,
    isLoading,
    error,
    refresh: fetchStaff,
    registerStaff,
    updateStaff,
    deleteStaff
  };
};
