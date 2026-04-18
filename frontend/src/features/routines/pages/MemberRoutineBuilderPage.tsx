import React, { useState, useEffect } from 'react';
import { 
  ChevronLeft, 
  Plus, 
  Trash2, 
  Dumbbell, 
  Search, 
  Save, 
  Loader2,
  CheckCircle2,
  AlertCircle,
  Copy,
  Layout
} from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { exerciseService } from '@/features/exercises/services/exercise.service';
import { routineService } from '@/features/routines/services/routine.service';
import { Exercise } from '@/features/exercises/types/exercise.types';
import { CreateTrainingDayRequest, CreateRoutineDetailRequest, RoutineSummary } from '../types/routine.types';
import { clsx } from 'clsx';
import { authService } from '@/features/auth/services/auth.service';

export const MemberRoutineBuilderPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const userRole = authService.getUserRole();
  const location = window.location.pathname;
  
  // Parámetros de contexto
  const memberId = searchParams.get('memberId');
  const requestId = searchParams.get('requestId');
  const memberName = searchParams.get('memberName');
  const editId = searchParams.get('editId'); // ID para modo EDICIÓN
  
  // Determinar el modo de operación
  const isBaseMode = location.includes('/trainer/routines/new-base');
  const isEditMode = !!editId;
  const isTrainerMode = userRole === 'TRAINER' || userRole === 'GYM_ADMIN';
  const isAssigningToMember = isTrainerMode && !isBaseMode && memberId;

  const [loading, setLoading] = useState(false);
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [baseRoutines, setBaseRoutines] = useState<RoutineSummary[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isExerciseModalOpen, setIsExerciseModalOpen] = useState(false);
  const [isBaseModalOpen, setIsBaseModalOpen] = useState(false);
  const [activeDayIndex, setActiveDayIndex] = useState(0);

  // Form State
  const [routineName, setRoutineName] = useState('');
  const [durationMonths, setDurationMonths] = useState<number | null>(3);
  const [days, setDays] = useState<any[]>([
    { dayName: 'Día 1', exercises: [] }
  ]);

  // EFECTO INICIAL: Cargar catálogo y, si es modo edición, la rutina existente
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [exData] = await Promise.all([exerciseService.getAll()]);
        setExercises(exData);
        
        if (isTrainerMode && !isEditMode) {
          const bases = await routineService.getBaseRoutines();
          setBaseRoutines(bases);
        }

        // MODO EDICIÓN: Cargar datos de la rutina a editar
        if (isEditMode) {
           const routineToEdit = await routineService.getById(Number(editId));
           setRoutineName(routineToEdit.name);
           
           // Mapear días con sus IDs originales para el PATCH
           const mappedDays = routineToEdit.days.map(d => ({
             id: d.id, // ID original del día
             dayName: d.name,
             exercises: d.exercises.map(ex => ({
               id: ex.id, // ID original del detalle
               exerciseId: ex.exerciseId,
               sets: ex.sets,
               repsMin: ex.repsMin,
               repsMax: ex.repsMax,
               targetRIR: ex.targetRIR,
               suggestedWeight: ex.suggestedWeight,
               notes: ex.notes
             }))
           }));
           setDays(mappedDays);
        } else {
           // MODO CREACIÓN: Valores por defecto
           setRoutineName(
             isBaseMode ? 'Nueva Plantilla Base' : 
             isAssigningToMember ? `Plan de Entrenamiento - ${memberName}` : 'Mi Rutina Personal'
           );
        }
      } catch (err) {
        console.error("Error inicializando el constructor", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [editId, isTrainerMode]);

  const handleAddDay = () => {
    if (days.length >= 7) return;
    const nextNumber = days.length + 1;
    setDays([...days, { dayName: `Día ${nextNumber}`, exercises: [] }]);
    setActiveDayIndex(days.length);
  };

  const handleRemoveDay = (index: number) => {
    if (days.length === 1) return;
    const newDays = days.filter((_, i) => i !== index).map((day, i) => ({ ...day, dayName: day.dayName || `Día ${i + 1}` }));
    setDays(newDays);
    setActiveDayIndex(Math.max(0, index - 1));
  };

  const handleAddExerciseToDay = (exercise: Exercise) => {
    const newDays = [...days];
    const newExercise = {
      exerciseId: exercise.id,
      sets: 3,
      repsMin: 8,
      repsMax: 12,
      targetRIR: 2,
      suggestedWeight: 0,
      notes: ''
    };
    newDays[activeDayIndex].exercises.push(newExercise);
    setDays(newDays);
    setIsExerciseModalOpen(false);
  };

  const handleUpdateExercise = (dayIndex: number, exIndex: number, field: string, value: any) => {
    const newDays = [...days];
    newDays[dayIndex].exercises[exIndex] = { ...newDays[dayIndex].exercises[exIndex], [field]: value };
    setDays(newDays);
  };

  const handleLoadBase = async (baseId: number) => {
    try {
      setLoading(true);
      const detail = await routineService.getById(baseId);
      if (isAssigningToMember && memberName) {
         setRoutineName(`${detail.name} - ${memberName}`);
      }
      
      const mappedDays = detail.days.map(d => ({
        dayName: d.name,
        exercises: d.exercises.map(ex => ({
          exerciseId: ex.exerciseId,
          sets: ex.sets,
          repsMin: ex.repsMin,
          repsMax: ex.repsMax,
          targetRIR: ex.targetRIR,
          suggestedWeight: ex.suggestedWeight,
          notes: ex.notes
        }))
      }));

      setDays(mappedDays);
      setIsBaseModalOpen(false);
    } catch (err) {
      alert("Error al cargar la rutina base");
    } finally {
      setLoading(false);
    }
  };

  const handleSaveRoutine = async () => {
    if (!routineName.trim()) return alert("Ingresa un nombre para la rutina");
    if (days.some(d => d.exercises.length === 0)) return alert("Todos los días deben tener ejercicios");

    setLoading(true);
    try {
      if (isEditMode) {
        // MODO EDICIÓN (PUT)
        await routineService.update(Number(editId), {
            name: routineName,
            days: days
        });
        navigate(-1);
      } else if (isBaseMode) {
        await routineService.createBase({
          name: routineName,
          days: days
        });
        navigate('/trainer/routines/bases');
      } else if (isAssigningToMember) {
        await routineService.assign({
          memberId: Number(memberId),
          requestId: requestId ? Number(requestId) : null,
          name: routineName,
          durationMonths: durationMonths || 3,
          days: days
        });
        navigate('/trainer/dashboard');
      } else {
        await routineService.createPersonal({
          name: routineName,
          days: days,
          durationMonths: durationMonths
        });
        navigate('/member/dashboard');
      }
    } catch (err) {
      alert("Error al guardar la rutina");
    } finally {
      setLoading(false);
    }
  };

  const filteredExercises = exercises.filter(ex => 
    ex.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-background pb-20 animate-in fade-in duration-700">
      <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-xl border-b border-white/[0.05] p-6 flex items-center justify-between">
         <div className="flex items-center gap-4">
           <Button onClick={() => navigate(-1)} variant="ghost" className="p-2 rounded-xl">
             <ChevronLeft />
           </Button>
           <div>
             <h1 className="text-sm font-black uppercase tracking-widest text-text-main leading-none mb-1">
                {isEditMode ? 'Editando Planificación' : isBaseMode ? 'Nueva Plantilla Base' : 'Constructor de Rutina'}
             </h1>
             <p className="text-[10px] font-bold text-primary uppercase tracking-tight italic">
                {isEditMode ? `ID: #${editId}` : isTrainerMode ? `Alumno: ${memberName || 'N/A'}` : 'Self-Service'}
             </p>
           </div>
         </div>

         <div className="flex gap-3">
            {isTrainerMode && !isEditMode && (
                <Button 
                    variant="secondary" 
                    className="px-6 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest gap-2"
                    onClick={() => setIsBaseModalOpen(true)}
                >
                    <Layout size={14} /> Usar Plantilla
                </Button>
            )}
            <Button 
                onClick={handleSaveRoutine} 
                variant="primary" 
                className="px-6 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest flex items-center gap-2 shadow-lg shadow-primary/20"
                disabled={loading}
            >
                {loading ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
                {isEditMode ? 'Actualizar Cambios' : isTrainerMode ? 'Asignar al Socio' : 'Guardar Plan'}
            </Button>
         </div>
      </header>

      <main className="max-w-4xl mx-auto p-6">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-6 mb-10">
          <div className={clsx(isEditMode ? "md:col-span-12" : "md:col-span-8")}>
            <label className="text-[10px] font-black uppercase tracking-[0.3em] text-text-secondary mb-3 block px-1">Nombre de la Rutina</label>
            <input 
              type="text" 
              value={routineName}
              onChange={(e) => setRoutineName(e.target.value)}
              className="w-full bg-surface-low border border-white/5 rounded-2xl p-6 text-3xl font-display font-black italic uppercase tracking-tighter text-text-main focus:outline-none focus:border-primary/50 transition-all"
              placeholder="EJ: PLAN DE FUERZA"
            />
          </div>
          {!isEditMode && (
            <div className="md:col-span-4">
                <label className="text-[10px] font-black uppercase tracking-[0.3em] text-text-secondary mb-3 block px-1">Duración del Plan</label>
                <select 
                value={durationMonths || ''}
                onChange={(e) => setDurationMonths(e.target.value ? Number(e.target.value) : null)}
                className="w-full h-[84px] bg-surface-low border border-white/5 rounded-2xl p-6 text-xl font-display font-black italic uppercase tracking-tight text-text-main focus:outline-none focus:border-primary/50 transition-all appearance-none cursor-pointer"
                >
                <option value="">Indefinido</option>
                <option value="1">1 Mes</option>
                <option value="2">2 Meses</option>
                <option value="3">3 Meses</option>
                <option value="6">6 Meses</option>
                <option value="12">1 Año</option>
                </select>
            </div>
          )}
        </div>

        <div className="flex items-center gap-3 overflow-x-auto pb-6 scrollbar-hide">
          {days.map((day, index) => (
            <button
              key={index}
              onClick={() => setActiveDayIndex(index)}
              className={clsx(
                "flex-shrink-0 px-6 py-3 rounded-2xl border font-black uppercase tracking-widest text-[10px] transition-all duration-300",
                activeDayIndex === index 
                  ? "bg-primary text-background border-primary shadow-lg" 
                  : "bg-surface-low text-text-secondary border-white/[0.05] hover:border-white/20"
              )}
            >
              {day.dayName}
            </button>
          ))}
          <button 
            onClick={handleAddDay}
            className="flex-shrink-0 w-10 h-10 rounded-2xl bg-surface-high border border-white/5 flex items-center justify-center text-primary hover:bg-primary/10 transition-all"
          >
            <Plus size={18} />
          </button>
        </div>

        {days[activeDayIndex] && (
            <div className="bg-surface-low rounded-[2.5rem] border border-white/[0.03] p-8 shadow-2xl animate-in slide-in-from-right-4 duration-500">
            <div className="flex items-center justify-between mb-8">
                <input 
                type="text"
                value={days[activeDayIndex].dayName}
                onChange={(e) => {
                    const newDays = [...days];
                    newDays[activeDayIndex].dayName = e.target.value;
                    setDays(newDays);
                }}
                className="bg-transparent text-2xl font-display font-black text-text-main italic uppercase tracking-tight focus:outline-none border-b border-white/5 focus:border-primary/50"
                />
                {days.length > 1 && (
                <button 
                    onClick={() => handleRemoveDay(activeDayIndex)}
                    className="p-2 text-error hover:bg-error/10 rounded-lg transition-colors"
                >
                    <Trash2 size={18} />
                </button>
                )}
            </div>

            <div className="flex flex-col gap-4">
                {days[activeDayIndex].exercises.map((ex: any, exIdx: number) => {
                const exerciseData = exercises.find(e => e.id === ex.exerciseId);
                return (
                    <div key={exIdx} className="bg-surface-high/20 border border-white/[0.02] rounded-2xl p-6 group hover:border-white/10 transition-all">
                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center gap-4">
                        <div className="w-12 h-12 rounded-lg bg-background overflow-hidden flex items-center justify-center text-primary border border-white/5 shadow-inner">
                            {exerciseData?.imageUrl ? (
                                <img src={exerciseData.imageUrl} alt="" className="w-full h-full object-cover" />
                            ) : (
                                <Dumbbell size={18} />
                            )}
                        </div>
                        <h4 className="font-bold text-text-main uppercase italic">{exerciseData?.name || 'Cargando...'}</h4>
                        </div>
                        <button 
                        onClick={() => {
                            const newDays = [...days];
                            newDays[activeDayIndex].exercises = newDays[activeDayIndex].exercises.filter((_: any, i: number) => i !== exIdx);
                            setDays(newDays);
                        }}
                        className="text-text-secondary opacity-30 hover:opacity-100 hover:text-error transition-all"
                        >
                        <Trash2 size={16} />
                        </button>
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary opacity-50 px-1">Sets</label>
                            <input 
                            type="number"
                            value={ex.sets}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'sets', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary opacity-50 px-1">Min Reps</label>
                            <input 
                            type="number"
                            value={ex.repsMin}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'repsMin', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary opacity-50 px-1">Max Reps</label>
                            <input 
                            type="number"
                            value={ex.repsMax}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'repsMax', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary opacity-50 px-1">Peso (kg)</label>
                            <input 
                            type="number"
                            value={ex.suggestedWeight}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'suggestedWeight', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-primary focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary opacity-50 px-1">RIR</label>
                            <input 
                            type="number"
                            value={ex.targetRIR}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'targetRIR', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-blue-400 focus:outline-none focus:border-primary/30"
                            />
                        </div>
                    </div>
                    </div>
                );
                })}

                <button 
                onClick={() => setIsExerciseModalOpen(true)}
                className="w-full py-10 border-2 border-dashed border-white/5 rounded-[2rem] flex flex-col items-center justify-center gap-2 text-text-secondary hover:border-primary/30 hover:text-primary transition-all group bg-surface-high/10"
                >
                <Plus size={24} className="group-hover:scale-110 transition-transform text-primary" />
                <span className="text-[10px] font-black uppercase tracking-widest">Añadir Ejercicio</span>
                </button>
            </div>
            </div>
        )}
      </main>

      <Modal isOpen={isExerciseModalOpen} onClose={() => setIsExerciseModalOpen(false)} title="Biblioteca Técnica">
        <div className="flex flex-col gap-6 py-4">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary opacity-50" size={18} />
            <Input placeholder="Buscar ejercicio..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="pl-12" />
          </div>
          <div className="grid grid-cols-1 gap-3 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
             {filteredExercises.map(ex => (
               <button key={ex.id} onClick={() => handleAddExerciseToDay(ex)} className="flex items-center gap-4 p-4 rounded-2xl bg-surface-high border border-white/5 hover:border-primary/40 transition-all text-left group">
                 <div className="w-14 h-14 rounded-xl overflow-hidden bg-background border border-white/5 flex-shrink-0">
                   {ex.imageUrl ? <img src={ex.imageUrl} alt={ex.name} className="w-full h-full object-cover group-hover:scale-110 transition-transform" /> : <div className="w-full h-full flex items-center justify-center text-primary/20"><Dumbbell size={20} /></div>}
                 </div>
                 <div className="flex-1">
                   <h5 className="text-sm font-bold text-text-main group-hover:text-primary transition-colors uppercase italic">{ex.name}</h5>
                   <p className="text-[9px] text-text-secondary font-black uppercase tracking-tighter opacity-50">{ex.isBase ? 'Ejercicio Base' : 'Personalizado'}</p>
                 </div>
                 <Plus size={18} className="text-primary opacity-0 group-hover:opacity-100 transition-all" />
               </button>
             ))}
          </div>
        </div>
      </Modal>

      <Modal isOpen={isBaseModalOpen} onClose={() => setIsBaseModalOpen(false)} title="Biblioteca de Plantillas">
        <div className="grid grid-cols-1 gap-3 py-4 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
            {baseRoutines.length === 0 ? (
                <div className="py-10 text-center text-text-secondary text-xs font-bold uppercase tracking-widest opacity-20">No hay plantillas creadas</div>
            ) : (
                baseRoutines.map(base => (
                    <button key={base.id} onClick={() => handleLoadBase(base.id)} className="w-full flex items-center justify-between p-6 rounded-2xl bg-surface-high border border-white/5 hover:border-primary/50 transition-all text-left group">
                        <div>
                            <h5 className="font-black text-text-main uppercase italic group-hover:text-primary transition-colors">{base.name}</h5>
                            <p className="text-[9px] text-text-secondary uppercase font-bold tracking-widest mt-1">Haga clic para cargar estructura</p>
                        </div>
                        <Copy size={18} className="text-primary opacity-0 group-hover:opacity-100 transition-all" />
                    </button>
                ))
            )}
        </div>
      </Modal>
    </div>
  );
};
