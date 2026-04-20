import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  CheckCircle2, 
  ChevronLeft, 
  Dumbbell, 
  History, 
  Play, 
  Save, 
  Timer,
  Info,
  ChevronRight,
  AlertCircle,
  Loader2,
  Trash2,
  Trophy
} from 'lucide-react';
import { useRoutine } from '@/features/routines/hooks/useRoutine';
import { useWorkoutTracker } from '@/features/tracker/hooks/useWorkoutTracker';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { clsx } from 'clsx';

interface SetRecord {
  reps: number;
  weight: number;
  rir: number;
  isLogged: boolean;
}

export const WorkoutTrackingPage = () => {
  const { routineId, dayId } = useParams();
  const navigate = useNavigate();
  const { detail, loading: routineLoading } = useRoutine(undefined, Number(routineId));
  const { session, startWorkout, logSet, finishWorkout, loading: trackerLoading } = useWorkoutTracker();

  const [currentDay, setCurrentDay] = useState<any>(null);
  const [activeExerciseIndex, setActiveExerciseIndex] = useState(0);
  const [setsRecords, setSetsRecords] = useState<Record<number, SetRecord[]>>({});
  const [timer, setTimer] = useState(0);
  const [restTimer, setRestTimer] = useState(0);
  const [isTimerActive, setIsTimerActive] = useState(false);
  const [isResting, setIsResting] = useState(false);
  const [isFinishing, setIsFinishing] = useState(false);

  useEffect(() => {
    if (detail && dayId) {
      const day = detail.days.find(d => d.id === Number(dayId));
      setCurrentDay(day);
      
      // Initialize sets records
      if (day) {
        const initialRecords: Record<number, SetRecord[]> = {};
        day.exercises.forEach(ex => {
          initialRecords[ex.exerciseId] = Array.from({ length: ex.sets }, () => ({
            reps: ex.repsMax,
            weight: ex.suggestedWeight,
            rir: ex.targetRIR,
            isLogged: false
          }));
        });
        setSetsRecords(initialRecords);
      }
    }
  }, [detail, dayId]);

  useEffect(() => {
    let interval: any;
    if (isTimerActive) {
      interval = setInterval(() => {
        setTimer(t => t + 1);
        if (isResting) {
          setRestTimer(rt => rt + 1);
        }
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isTimerActive, isResting]);

  const handleStartSession = async () => {
    if (!dayId || !routineId) return;
    try {
      await startWorkout(Number(routineId), Number(dayId));
      setIsTimerActive(true);
    } catch (err: any) {
      // Error handled by hook and displayed in UI if needed, 
      // but we show alert for immediate feedback
      alert(err.response?.data?.message || "No puedes iniciar el entrenamiento. Verifica tu suscripción.");
    }
  };

  const handleLogSet = async (exerciseId: number, setIndex: number) => {
    const record = setsRecords[exerciseId][setIndex];
    try {
      await logSet(exerciseId, setIndex + 1, record.reps, record.weight, record.rir);
      
      const newRecords = { ...setsRecords };
      newRecords[exerciseId][setIndex].isLogged = true;
      setSetsRecords(newRecords);
      
      // Start rest timer
      setRestTimer(0);
      setIsResting(true);
    } catch (err) {
      alert("Error al registrar la serie");
    }
  };

  const handleUpdateRecord = (exerciseId: number, setIndex: number, field: keyof SetRecord, value: any) => {
    const newRecords = { ...setsRecords };
    newRecords[exerciseId][setIndex] = { ...newRecords[exerciseId][setIndex], [field]: value };
    setSetsRecords(newRecords);
  };

  const handleFinish = async () => {
    try {
      await finishWorkout();
      setIsFinishing(true);
    } catch (err) {
      alert("Error al finalizar entrenamiento");
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  if (routineLoading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Preparando Estación...</p>
    </div>
  );

  if (!currentDay) return null;

  const activeExercise = currentDay.exercises[activeExerciseIndex];
  const allExercisesCompleted = currentDay.exercises.every((ex: any) => 
    setsRecords[ex.exerciseId]?.every(s => s.isLogged)
  );

  if (isFinishing) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center p-10 text-center">
        <div className="w-24 h-24 bg-primary/20 rounded-full flex items-center justify-center mb-8 shadow-[0_0_50px_rgba(255,182,0,0.2)]">
          <Trophy size={48} className="text-primary" />
        </div>
        <h2 className="text-5xl font-display font-black text-text-main italic mb-4 uppercase">¡Entrenamiento Finalizado!</h2>
        <p className="text-text-secondary text-lg font-medium mb-12 max-w-md">
          Has completado todos los objetivos de hoy. Tus progresos han sido guardados exitosamente.
        </p>
        <Button onClick={() => navigate('/member/dashboard')} variant="primary" className="px-12 py-5 rounded-2xl font-black uppercase tracking-widest">
          Volver al Dashboard
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background pb-32">
      {/* STICKY HEADER */}
      <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-xl border-b border-white/[0.05] p-6 flex items-center justify-between">
         <div className="flex items-center gap-4">
           <Button onClick={() => navigate(-1)} variant="ghost" className="p-2 rounded-xl">
             <ChevronLeft />
           </Button>
           <div>
             <h1 className="text-base font-black uppercase tracking-widest text-text-main leading-none mb-1">{currentDay.name}</h1>
             <p className="text-[10px] font-bold text-primary uppercase tracking-tight">{detail?.name}</p>
           </div>
         </div>

         <div className="flex items-center gap-3">
           {isResting && (
             <div className="flex items-center gap-2 bg-primary/10 px-3 py-2 rounded-2xl border border-primary/20 animate-in fade-in zoom-in duration-300">
               <History size={14} className="text-primary animate-spin-slow" />
               <div className="flex flex-col">
                 <span className="text-[8px] font-black uppercase tracking-tighter text-primary/60 leading-none">Descanso</span>
                 <span className="text-xs font-mono font-black text-primary leading-none mt-0.5">{formatTime(restTimer)}</span>
               </div>
             </div>
           )}
           <div className="flex items-center gap-4 bg-surface-low px-4 py-2 rounded-2xl border border-white/5 shadow-inner">
             <Timer size={14} className={clsx("text-primary", isTimerActive && "animate-pulse")} />
             <span className="text-sm font-mono font-black text-text-main tracking-widest">{formatTime(timer)}</span>
           </div>
         </div>
      </header>

      {!session ? (
        <div className="p-8 flex flex-col items-center justify-center min-h-[70vh] text-center animate-in fade-in duration-1000">
           <div className="w-20 h-20 bg-primary/10 rounded-[2rem] flex items-center justify-center text-primary mb-8 border border-primary/20">
             <Play size={32} className="ml-1" />
           </div>
           <h2 className="text-5xl font-display font-black text-text-main italic mb-4 uppercase tracking-tighter">¿Listo para empezar?</h2>
           <p className="text-text-secondary mb-10 max-w-xs font-medium">Inicia la sesión para comenzar a registrar tus series y tiempos de descanso.</p>
           <Button onClick={handleStartSession} variant="primary" className="w-full max-w-xs py-5 rounded-2xl text-sm font-black uppercase tracking-[0.2em] shadow-2xl">
              Iniciar Sesión
           </Button>
        </div>
      ) : (
        <div className="max-w-4xl mx-auto p-6 animate-in slide-in-from-right-4 duration-500">
           {/* EXERCISE SELECTOR (TABS) */}
           <div className="flex items-center gap-3 overflow-x-auto pb-6 scrollbar-hide">
             {currentDay.exercises.map((ex: any, index: number) => {
               const isCompleted = setsRecords[ex.exerciseId]?.every(s => s.isLogged);
               return (
                 <button 
                   key={ex.exerciseId}
                   onClick={() => setActiveExerciseIndex(index)}
                   className={clsx(
                     "flex-shrink-0 px-6 py-3 rounded-2xl border font-black uppercase tracking-widest text-[10px] transition-all duration-300 flex items-center gap-2",
                     activeExerciseIndex === index 
                       ? "bg-primary text-background border-primary shadow-[0_10px_20px_rgba(255,182,0,0.2)]" 
                       : "bg-surface-low text-text-secondary border-white/[0.05] hover:border-white/20",
                     isCompleted && activeExerciseIndex !== index && "opacity-40"
                   )}
                 >
                   {isCompleted && <CheckCircle2 size={12} />}
                   Eje {index + 1}
                 </button>
               );
             })}
           </div>

           {/* ACTIVE EXERCISE DETAIL */}
           <div className="bg-surface-low rounded-[2.5rem] border border-white/[0.03] p-8 shadow-2xl mb-8">
              <div className="flex flex-col md:flex-row gap-8 items-start mb-10">
                <div className="w-32 h-32 rounded-[2rem] overflow-hidden border border-white/10 bg-background flex-shrink-0 shadow-xl">
                  {activeExercise.exerciseImageUrl ? (
                    <img src={activeExercise.exerciseImageUrl} alt={activeExercise.exerciseName} className="w-full h-full object-cover" />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-primary/10">
                       <Dumbbell size={40} />
                    </div>
                  )}
                </div>
                <div className="flex-1">
                  <h3 className="text-4xl font-display font-black text-text-main italic uppercase tracking-tight mb-2 leading-none">
                    {activeExercise.exerciseName}
                  </h3>
                  <div className="flex flex-wrap gap-4 mt-4">
                    <span className="bg-surface-high/50 px-3 py-1.5 rounded-lg text-[10px] font-black text-text-secondary uppercase border border-white/5">
                      OBJETIVO: {activeExercise.sets}x{activeExercise.repsMin}-{activeExercise.repsMax} @ {activeExercise.suggestedWeight}kg (RIR {activeExercise.targetRIR})
                    </span>
                  </div>
                  {activeExercise.notes && (
                    <p className="mt-4 text-xs font-medium text-text-secondary italic">
                      💡 {activeExercise.notes}
                    </p>
                  )}
                </div>
              </div>

              {/* SETS TABLE */}
              <div className="flex flex-col gap-4">
                 <div className="grid grid-cols-12 gap-4 px-4 mb-2">
                    <div className="col-span-1 text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">#</div>
                    <div className="col-span-4 text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">Kg</div>
                    <div className="col-span-4 text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">Reps</div>
                    <div className="col-span-2 text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">RIR</div>
                    <div className="col-span-1"></div>
                 </div>

                 {setsRecords[activeExercise.exerciseId]?.map((record, idx) => (
                    <div 
                      key={idx} 
                      className={clsx(
                        "grid grid-cols-12 gap-4 items-center p-3 rounded-2xl transition-all duration-500",
                        record.isLogged ? "bg-primary/5 opacity-60 grayscale-[50%]" : "bg-surface-high/20 border border-white/[0.02]"
                      )}
                    >
                      <div className="col-span-1 flex justify-center">
                        <span className="font-display font-black text-text-secondary">{idx + 1}</span>
                      </div>
                      
                      <div className="col-span-4 px-2">
                        <input 
                          type="number"
                          value={record.weight}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'weight', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-xl py-3 px-2 text-center text-sm font-black text-primary focus:outline-none focus:border-primary/50 transition-colors"
                        />
                      </div>

                      <div className="col-span-4 px-2">
                        <input 
                          type="number"
                          value={record.reps}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'reps', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-xl py-3 px-2 text-center text-sm font-black text-text-main focus:outline-none focus:border-white/20 transition-colors"
                        />
                      </div>

                      <div className="col-span-2 px-1">
                        <select 
                          value={record.rir}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'rir', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-xl py-3 px-1 text-center text-xs font-black text-primary focus:outline-none focus:border-primary/50 appearance-none"
                        >
                          {[0,1,2,3,4,5].map(v => <option key={v} value={v}>{v}</option>)}
                        </select>
                      </div>

                      <div className="col-span-1 flex justify-end">
                        <button 
                          onClick={() => handleLogSet(activeExercise.exerciseId, idx)}
                          disabled={record.isLogged}
                          className={clsx(
                            "w-10 h-10 rounded-xl flex items-center justify-center transition-all duration-300",
                            record.isLogged 
                              ? "bg-green-500 text-background" 
                              : "bg-surface-high hover:bg-primary/20 text-text-secondary hover:text-primary"
                          )}
                        >
                          {record.isLogged ? <CheckCircle2 size={18} /> : <Save size={18} />}
                        </button>
                      </div>
                    </div>
                 ))}
              </div>
           </div>

           {/* NAVIGATION BUTTONS */}
           <div className="flex items-center justify-between gap-6">
              <Button 
                onClick={() => setActiveExerciseIndex(prev => Math.max(0, prev - 1))}
                variant="ghost" 
                className="flex-1 py-5 rounded-2xl text-[10px] font-black uppercase tracking-widest text-text-secondary"
                disabled={activeExerciseIndex === 0}
              >
                Anterior
              </Button>
              {activeExerciseIndex < currentDay.exercises.length - 1 ? (
                <Button 
                  onClick={() => setActiveExerciseIndex(prev => prev + 1)}
                  variant="primary" 
                  className="flex-[2] py-5 rounded-2xl text-[10px] font-black uppercase tracking-[0.2em] shadow-xl"
                >
                  Siguiente Ejercicio
                </Button>
              ) : (
                <Button 
                  onClick={handleFinish}
                  variant="primary" 
                  className="flex-[2] py-5 rounded-2xl text-[10px] font-black uppercase tracking-[0.2em] shadow-[0_15px_30px_rgba(255,182,0,0.2)]"
                  disabled={!allExercisesCompleted || trackerLoading}
                >
                  {trackerLoading ? <Loader2 className="animate-spin" /> : "Finalizar Entrenamiento"}
                </Button>
              )}
           </div>
        </div>
      )}
    </div>
  );
};
