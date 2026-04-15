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
  AlertCircle
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { exerciseService } from '@/features/exercises/services/exercise.service';
import { routineService } from '@/features/routines/services/routine.service';
import { Exercise } from '@/features/exercises/types/exercise.types';
import { CreatePersonalRoutineRequest, CreateTrainingDayRequest, CreateRoutineDetailRequest } from '../types/routine.types';
import { clsx } from 'clsx';

export const MemberRoutineBuilderPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isExerciseModalOpen, setIsExerciseModalOpen] = useState(false);
  const [activeDayIndex, setActiveDayIndex] = useState(0);

  // Form State
  const [routineName, setRoutineName] = useState('Mi Rutina Personal');
  const [days, setDays] = useState<CreateTrainingDayRequest[]>([
    { dayName: 'Día 1', exercises: [] }
  ]);

  useEffect(() => {
    const fetchExercises = async () => {
      try {
        const data = await exerciseService.getAll();
        setExercises(data);
      } catch (err) {
        console.error("Error fetching exercises", err);
      }
    };
    fetchExercises();
  }, []);

  const handleAddDay = () => {
    setDays([...days, { dayName: `Día ${days.length + 1}`, exercises: [] }]);
    setActiveDayIndex(days.length);
  };

  const handleRemoveDay = (index: number) => {
    if (days.length === 1) return;
    const newDays = days.filter((_, i) => i !== index);
    setDays(newDays);
    setActiveDayIndex(Math.max(0, index - 1));
  };

  const handleAddExerciseToDay = (exercise: Exercise) => {
    const newDays = [...days];
    const newExercise: CreateRoutineDetailRequest = {
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

  const handleRemoveExerciseFromDay = (dayIndex: number, exIndex: number) => {
    const newDays = [...days];
    newDays[dayIndex].exercises = newDays[dayIndex].exercises.filter((_, i) => i !== exIndex);
    setDays(newDays);
  };

  const handleUpdateExercise = (dayIndex: number, exIndex: number, field: keyof CreateRoutineDetailRequest, value: any) => {
    const newDays = [...days];
    newDays[dayIndex].exercises[exIndex] = { ...newDays[dayIndex].exercises[exIndex], [field]: value };
    setDays(newDays);
  };

  const handleSaveRoutine = async () => {
    if (!routineName.trim()) {
      alert("Por favor ingresa un nombre para la rutina");
      return;
    }
    if (days.some(d => d.exercises.length === 0)) {
      alert("Todos los días deben tener al menos un ejercicio");
      return;
    }

    setLoading(true);
    try {
      await routineService.createPersonal({
        name: routineName,
        days: days
      });
      navigate('/member/dashboard');
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
      {/* HEADER */}
      <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-xl border-b border-white/[0.05] p-6 flex items-center justify-between">
         <div className="flex items-center gap-4">
           <Button onClick={() => navigate(-1)} variant="ghost" className="p-2 rounded-xl">
             <ChevronLeft />
           </Button>
           <div>
             <h1 className="text-sm font-black uppercase tracking-widest text-text-main leading-none mb-1">Constructor de Rutina</h1>
             <p className="text-[10px] font-bold text-primary uppercase tracking-tight italic">Self-Service</p>
           </div>
         </div>

         <Button 
          onClick={handleSaveRoutine} 
          variant="primary" 
          className="px-6 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest flex items-center gap-2"
          disabled={loading}
         >
           {loading ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
           Guardar Plan
         </Button>
      </header>

      <main className="max-w-4xl mx-auto p-6">
        <div className="mb-10">
          <label className="text-[10px] font-black uppercase tracking-[0.3em] text-text-secondary mb-3 block px-1">Nombre de la Rutina</label>
          <input 
            type="text" 
            value={routineName}
            onChange={(e) => setRoutineName(e.target.value)}
            className="w-full bg-surface-low border border-white/5 rounded-2xl p-6 text-3xl font-display font-black italic uppercase tracking-tighter text-text-main focus:outline-none focus:border-primary/50 transition-all"
            placeholder="EJ: MI PLAN DE VOLUMEN"
          />
        </div>

        {/* DAYS TABS */}
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

        {/* ACTIVE DAY EDITOR */}
        <div className="bg-surface-low rounded-[2.5rem] border border-white/[0.03] p-8 shadow-2xl">
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
            {days[activeDayIndex].exercises.map((ex, exIdx) => {
              const exerciseData = exercises.find(e => e.id === ex.exerciseId);
              return (
                <div key={exIdx} className="bg-surface-high/20 border border-white/[0.02] rounded-2xl p-6 group hover:border-white/10 transition-all">
                  <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-4">
                       <div className="w-10 h-10 rounded-lg bg-background flex items-center justify-center text-primary border border-white/5">
                         <Dumbbell size={18} />
                       </div>
                       <h4 className="font-bold text-text-main uppercase italic">{exerciseData?.name || 'Cargando...'}</h4>
                    </div>
                    <button 
                      onClick={() => handleRemoveExerciseFromDay(activeDayIndex, exIdx)}
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
              className="w-full py-6 border-2 border-dashed border-white/5 rounded-2xl flex flex-col items-center justify-center gap-2 text-text-secondary hover:border-primary/30 hover:text-primary transition-all group"
            >
              <Plus size={24} className="group-hover:scale-110 transition-transform" />
              <span className="text-[10px] font-black uppercase tracking-widest">Añadir Ejercicio</span>
            </button>
          </div>
        </div>
      </main>

      {/* EXERCISE SELECTION MODAL */}
      <Modal
        isOpen={isExerciseModalOpen}
        onClose={() => setIsExerciseModalOpen(false)}
        title="Seleccionar Ejercicio"
      >
        <div className="flex flex-col gap-6 py-4">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary opacity-50" size={18} />
            <Input 
              placeholder="Buscar por nombre..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-12"
            />
          </div>

          <div className="grid grid-cols-1 gap-3 max-h-[400px] overflow-y-auto pr-2 scrollbar-hide">
             {filteredExercises.map(ex => (
               <button 
                 key={ex.id}
                 onClick={() => handleAddExerciseToDay(ex)}
                 className="flex items-center gap-4 p-4 rounded-xl bg-surface-high border border-white/5 hover:border-primary/40 transition-all text-left group"
               >
                 <div className="w-12 h-12 rounded-lg overflow-hidden bg-background border border-white/5">
                   {ex.imageUrl ? (
                     <img src={ex.imageUrl} alt={ex.name} className="w-full h-full object-cover" />
                   ) : (
                     <div className="w-full h-full flex items-center justify-center text-primary/20">
                       <Dumbbell size={20} />
                     </div>
                   )}
                 </div>
                 <div className="flex-1">
                   <h5 className="text-sm font-bold text-text-main group-hover:text-primary transition-colors uppercase italic">{ex.name}</h5>
                   <p className="text-[10px] text-text-secondary font-medium uppercase tracking-tighter">
                     Ejercicio Personalizable
                   </p>
                 </div>
                 <Plus size={18} className="text-primary opacity-0 group-hover:opacity-100 transition-all" />
               </button>
             ))}
          </div>
        </div>
      </Modal>
    </div>
  );
};
