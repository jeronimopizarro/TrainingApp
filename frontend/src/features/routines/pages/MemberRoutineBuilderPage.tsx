import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '@/features/auth/services/auth.service';
import { exerciseService } from '@/features/exercises/services/exercise.service';
import { routineService } from '@/features/routines/services/routine.service';
import { Exercise } from '@/features/exercises/types/exercise.types';
import { RoutineSummary, RoutineDetail } from '../types/routine.types';
import { RoutineBuilder } from '../components/RoutineBuilder';
import { Loader2 } from 'lucide-react';

export const MemberRoutineBuilderPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const userData = authService.getUserData();
  const userRole = authService.getUserRole();
  const location = window.location.pathname;
  
  // Context Parameters
  const memberId = searchParams.get('memberId');
  const requestId = searchParams.get('requestId');
  const memberName = searchParams.get('memberName');
  const editId = searchParams.get('editId');
  
  // Modes
  const isBaseMode = location.includes('/trainer/routines/new-base');
  const isEditMode = !!editId;
  const isTrainerMode = userRole === 'TRAINER' || userRole === 'GYM_ADMIN';
  const isAssigningToMember = isTrainerMode && !isBaseMode && memberId;

  // Data State
  const [loading, setLoading] = useState(true);
  const [saveLoading, setSaveLoading] = useState(false);
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [baseRoutines, setBaseRoutines] = useState<RoutineSummary[]>([]);
  const [initialRoutine, setInitialRoutine] = useState<RoutineDetail | null>(null);

  useEffect(() => {
    const init = async () => {
      try {
        setLoading(true);
        const [exData] = await Promise.all([exerciseService.getAll()]);
        setExercises(exData);
        
        if (isTrainerMode && !isEditMode) {
          const bases = await routineService.getBaseRoutines();
          setBaseRoutines(bases);
        }

        if (isEditMode) {
           const routineToEdit = await routineService.getById(Number(editId));
           
           // Security validation
           if (!isTrainerMode && routineToEdit.createdByUserId !== userData?.userId) {
             navigate('/member/dashboard');
             return;
           }
           setInitialRoutine(routineToEdit);
        }
      } catch (err) {
        console.error("Error initializing builder", err);
      } finally {
        setLoading(false);
      }
    };
    init();
  }, [editId, isTrainerMode, userData?.userId]);

  const handleSave = async (payload: any) => {
    setSaveLoading(true);
    try {
      if (isEditMode) {
        await routineService.update(Number(editId), payload);
        navigate(-1);
      } else if (isBaseMode) {
        await routineService.createBase(payload);
        navigate('/trainer/routines/bases');
      } else if (isAssigningToMember) {
        await routineService.assign({
          memberId: Number(memberId),
          requestId: requestId ? Number(requestId) : null,
          name: payload.name,
          durationMonths: payload.durationMonths || 3,
          days: payload.days
        });
        navigate('/trainer/dashboard');
      } else {
        await routineService.createPersonal({
          name: payload.name,
          days: payload.days,
          durationMonths: payload.durationMonths
        });
        navigate('/member/dashboard');
      }
    } catch (err) {
      alert("Error al guardar la rutina. Verifica que todos los campos sean válidos.");
    } finally {
      setSaveLoading(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Configurando Constructor...</p>
    </div>
  );

  return (
    <RoutineBuilder 
      initialData={initialRoutine}
      isEditMode={isEditMode}
      isBaseMode={isBaseMode}
      isTrainerMode={isTrainerMode}
      memberName={memberName}
      exercises={exercises}
      baseRoutines={baseRoutines}
      loading={saveLoading}
      onSave={handleSave}
      onCancel={() => navigate(-1)}
    />
  );
};
