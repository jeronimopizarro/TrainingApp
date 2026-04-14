import { useState, useEffect, useCallback } from 'react';
import { membershipService } from '../services/membership.service';
import { MembershipPlan, CreateMembershipPlanRequest, UpdateMembershipPlanRequest } from '../types/membership.types';
import { authService } from '@/features/auth/services/auth.service';

export const useMemberships = () => {
  const [plans, setPlans] = useState<MembershipPlan[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPlans = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');
      
      const data = await membershipService.getAllByGym(user.gymId);
      setPlans(data);
    } catch (err: any) {
      setError('Error al cargar planes: ' + (err.message || 'Verifica tu conexión.'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPlans();
  }, [fetchPlans]);

  const createPlan = async (request: Omit<CreateMembershipPlanRequest, 'gymId'>) => {
    setIsLoading(true);
    try {
      const user = authService.getUserData();
      if (!user?.gymId) throw new Error('No se pudo identificar el gimnasio.');
      
      await membershipService.create({ ...request, gymId: user.gymId });
      await fetchPlans();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al crear plan.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const updatePlan = async (id: number, request: UpdateMembershipPlanRequest) => {
    setIsLoading(true);
    try {
      await membershipService.update(id, request);
      await fetchPlans();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al actualizar plan.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const deletePlan = async (id: number) => {
    try {
      await membershipService.delete(id);
      await fetchPlans();
      return true;
    } catch (err) {
      setError('No se pudo eliminar el plan.');
      return false;
    }
  };

  return {
    plans,
    isLoading,
    error,
    refresh: fetchPlans,
    createPlan,
    updatePlan,
    deletePlan
  };
};
