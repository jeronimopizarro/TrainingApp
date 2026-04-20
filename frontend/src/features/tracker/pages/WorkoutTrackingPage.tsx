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
  AlertCircle,
  Loader2,
  Trash2,
  Trophy,
  StopCircle,
  MoreVertical
} from 'lucide-react';
import { useRoutine } from '@/features/routines/hooks/useRoutine';
import { useWorkoutTracker } from '@/features/tracker/hooks/useWorkoutTracker';
import { Button } from '@/shared/components/Button';
import { Modal } from '@/shared/components/Modal';
import { VideoModal } from '@/shared/components/VideoModal';
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
  
  const { session, startWorkout, logSet, finishWorkout, cancelWorkout, loading: trackerLoading } = useWorkoutTracker();
  
  const effectiveRoutineId = session?.routineId || Number(routineId);
  const effectiveDayId = session?.trainingDayId || Number(dayId);

  const { detail, loading: routineLoading } = useRoutine(undefined, effectiveRoutineId);

  const [currentDay, setCurrentDay] = useState<any>(null);
  const [activeExerciseIndex, setActiveExerciseIndex] = useState(0);
  const [setsRecords, setSetsRecords] = useState<Record<number, SetRecord[]>>({});
  const [timer, setTimer] = useState(0);
  const [restTimer, setRestTimer] = useState(0);
  const [isTimerActive, setIsTimerActive] = useState(false);
  const [isResting, setIsResting] = useState(false);
  const [isFinishing, setIsFinishing] = useState(false);
  
  // Modal states
  const [showResumeModal, setShowResumeModal] = useState(false);
  const [showOptionsModal, setShowOptionsModal] = useState(false);
  const [isUserInteracting, setIsUserInteracting] = useState(false);

  // Video Player State
  const [videoPlayer, setVideoPlayer] = useState<{ isOpen: boolean, url: string, title: string }>({
    isOpen: false,
    url: '',
    title: ''
  });

  // Efecto para detectar si hay una sesión pendiente al entrar
  useEffect(() => {
    if (session && !isUserInteracting) {
       setShowResumeModal(true);
    }
  }, [session, isUserInteracting]);

  // Efecto para sincronizar el día y las series
  useEffect(() => {
    if (detail && effectiveDayId) {
      const day = detail.days.find(d => d.id === effectiveDayId);
      setCurrentDay(day);
      
      if (day) {
        const initialRecords: Record<number, SetRecord[]> = {};
        
        day.exercises.forEach(ex => {
          const loggedForThisEx = session?.loggedSets.filter(s => s.exerciseId === ex.exerciseId) || [];
          
          initialRecords[ex.exerciseId] = Array.from({ length: ex.sets }, (_, i) => {
            const loggedSet = loggedForThisEx.find(s => s.setNumber === i + 1);
            
            if (loggedSet) {
              return {
                reps: loggedSet.repsPerformed,
                weight: Number(loggedSet.weightLifted),
                rir: loggedSet.rir,
                isLogged: true
              };
            }

            return {
              reps: ex.repsMax,
              weight: ex.suggestedWeight,
              rir: ex.targetRIR,
              isLogged: false
            };
          });
        });
        
        setSetsRecords(initialRecords);

        if (session) {
           setIsTimerActive(true);
           const start = new Date(session.startTime).getTime();
           const now = new Date().getTime();
           setTimer(Math.floor((now - start) / 1000));
        }
      }
    }
  }, [detail, effectiveDayId, session]);

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
      setIsUserInteracting(true);
      await startWorkout(Number(routineId), Number(dayId));
    } catch (err: any) {
      if (!err.response?.data?.message?.includes('Ya tienes un entrenamiento en progreso')) {
          alert(err.response?.data?.message || "No puedes iniciar el entrenamiento.");
      }
    }
  };

  const handleResumeSession = () => {
    setShowResumeModal(false);
    setIsUserInteracting(true);
  };

  const handleCancelSession = async () => {
    if (window.confirm("¿Estás seguro de cancelar el entrenamiento actual? Se perderán los datos no guardados.")) {
      try {
        await cancelWorkout();
        setShowResumeModal(false);
        setShowOptionsModal(false);
        setIsUserInteracting(false);
      } catch (err) {
        alert("Error al cancelar la sesión");
      }
    }
  };

  const handleLogSet = async (exerciseId: number, setIndex: number) => {
    const record = setsRecords[exerciseId][setIndex];
    try {
      await logSet(exerciseId, setIndex + 1, record.reps, record.weight, record.rir);
      
      const newRecords = { ...setsRecords };
      newRecords[exerciseId][setIndex].isLogged = true;
      setSetsRecords(newRecords);
      
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
      setShowOptionsModal(false);
      setIsFinishing(true);
    } catch (err) {
      alert("Error al finalizar entrenamiento");
    }
  };

  const handleNextExercise = () => {
    if (activeExerciseIndex < currentDay.exercises.length - 1) {
      setActiveExerciseIndex(prev => prev + 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handlePrevExercise = () => {
    setActiveExerciseIndex(prev => Math.max(0, prev - 1));
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  if (routineLoading || trackerLoading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Sincronizando Estación...</p>
    </div>
  );

  if (!currentDay) return (
     <div className="min-h-screen bg-background flex flex-col items-center justify-center p-10 text-center">
        <AlertCircle size={48} className="text-error mb-4" />
        <h2 className="text-xl font-bold text-text-main">No se pudo cargar el día de entrenamiento</h2>
        <Button onClick={() => navigate('/member/dashboard')} variant="ghost" className="mt-6">Volver al inicio</Button>
     </div>
  );

  const activeExercise = currentDay.exercises[activeExerciseIndex];
  const allExercisesCompleted = currentDay.exercises.every((ex: any) => 
    setsRecords[ex.exerciseId]?.every(s => s.isLogged)
  );

  if (isFinishing) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center p-6 sm:p-10 text-center">
        <div className="w-20 h-20 sm:w-24 sm:h-24 bg-primary/20 rounded-full flex items-center justify-center mb-6 sm:mb-8 shadow-[0_0_50px_rgba(255,182,0,0.2)]">
          <Trophy size={40} className="text-primary sm:size-[48px]" />
        </div>
        <h2 className="text-3xl sm:text-5xl font-display font-black text-text-main italic mb-4 uppercase tracking-tight leading-tight">
          ¡Entrenamiento <span className="text-primary">Finalizado</span>!
        </h2>
        <p className="text-text-secondary text-sm sm:text-lg font-medium mb-8 sm:mb-12 max-w-md">
          Has completado todos los objetivos de hoy. Tus progresos han sido guardados exitosamente.
        </p>
        <Button onClick={() => navigate('/member/dashboard')} variant="primary" className="w-full sm:w-auto px-12 py-5 rounded-2xl font-black uppercase tracking-widest text-xs">
          Volver al Dashboard
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background pb-32">
      <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-xl border-b border-white/[0.05] p-4 sm:p-6 flex items-center justify-between gap-2">
         <div className="flex items-center gap-2 sm:gap-4 flex-1 min-w-0">
           <Button onClick={() => navigate(-1)} variant="ghost" className="p-1.5 sm:p-2 rounded-xl shrink-0">
             <ChevronLeft size={20} />
           </Button>
           <div className="min-w-0">
             <h1 className="text-sm sm:text-base font-black uppercase tracking-widest text-text-main leading-none mb-1 truncate">{currentDay.name}</h1>
             <p className="text-[9px] sm:text-[10px] font-bold text-primary uppercase tracking-tight truncate">{detail?.name}</p>
           </div>
         </div>

         <div className="flex items-center gap-2 sm:gap-3 shrink-0">
           {isResting && (
             <div className="flex items-center gap-1.5 sm:gap-2 bg-primary/10 px-2 py-1.5 sm:px-3 sm:py-2 rounded-xl sm:rounded-2xl border border-primary/20 animate-in fade-in zoom-in duration-300">
               <History size={12} className="text-primary animate-spin-slow shrink-0" />
               <div className="flex flex-col">
                 <span className="text-[7px] font-black uppercase tracking-tighter text-primary/60 leading-none">Rest</span>
                 <span className="text-[10px] sm:text-xs font-mono font-black text-primary leading-none mt-0.5">{formatTime(restTimer)}</span>
               </div>
             </div>
           )}
           <div className="flex items-center gap-2 sm:gap-3 bg-surface-low px-2.5 py-1.5 sm:px-4 sm:py-2 rounded-xl sm:rounded-2xl border border-white/5 shadow-inner">
             <Timer size={12} className={clsx("text-primary shrink-0", isTimerActive && "animate-pulse")} />
             <span className="text-[10px] sm:text-sm font-mono font-black text-text-main tracking-widest">{formatTime(timer)}</span>
           </div>
           
           {session && (
             <button 
                onClick={() => setShowOptionsModal(true)}
                className="p-2 sm:p-3 bg-surface-high hover:bg-surface-med rounded-xl sm:rounded-2xl border border-white/5 text-text-secondary hover:text-text-main transition-all shrink-0"
             >
                <MoreVertical size={16} />
             </button>
           )}
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

           <div className="bg-surface-low rounded-[1.5rem] sm:rounded-[2.5rem] border border-white/[0.03] p-5 sm:p-8 shadow-2xl mb-8">
              <div className="flex flex-col sm:flex-row gap-6 sm:gap-8 items-start mb-8 sm:mb-10">
                <div className="w-24 h-24 sm:w-32 sm:h-32 rounded-[1.5rem] sm:rounded-[2rem] overflow-hidden border border-white/10 bg-background flex-shrink-0 shadow-xl mx-auto sm:mx-0">
                  {activeExercise.exerciseImageUrl ? (
                    <img src={activeExercise.exerciseImageUrl} alt={activeExercise.exerciseName} className="w-full h-full object-cover" />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-primary/10">
                       <Dumbbell size={32} className="sm:size-[40px]" />
                    </div>
                  )}
                </div>
                <div className="flex-1 text-center sm:text-left w-full">
                  <h3 className="text-2xl sm:text-4xl font-display font-black text-text-main italic uppercase tracking-tight mb-2 leading-tight">
                    {activeExercise.exerciseName}
                  </h3>
                  <div className="flex flex-wrap justify-center sm:justify-start gap-3 sm:gap-4 mt-4">
                    {activeExercise.exerciseVideoUrl && (
                      <button 
                        onClick={() => setVideoPlayer({ isOpen: true, url: activeExercise.exerciseVideoUrl, title: activeExercise.exerciseName })}
                        className="flex items-center gap-2 px-3 py-2 bg-primary/10 hover:bg-primary/20 text-primary text-[9px] sm:text-[10px] font-black rounded-lg uppercase tracking-widest border border-primary/20 transition-all"
                      >
                        <Play size={12} className="fill-primary" />
                        Ver técnica
                      </button>
                    )}
                    </div>
                    {activeExercise.exerciseDescription && (
                    <p className="mt-4 text-xs sm:text-sm text-text-secondary leading-relaxed max-w-2xl border-l-2 border-primary/20 pl-4 text-left">
                      {activeExercise.exerciseDescription}
                    </p>
                    )}
                    {activeExercise.notes && (
                    <p className="mt-4 text-[10px] sm:text-xs font-medium text-text-secondary italic text-left">
                      💡 {activeExercise.notes}
                    </p>
                  )}
                </div>
              </div>

              <div className="flex flex-col gap-3 sm:gap-4">
                 {/* HEADER DE SERIES - OCULTO EN MOBILE SI ES MUY PEQUEÑO, PERO AQUÍ LO ADAPTAMOS */}
                 <div className="grid grid-cols-12 gap-2 sm:gap-4 px-2 sm:px-4 mb-1">
                    <div className="col-span-1 text-[8px] sm:text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">#</div>
                    <div className="col-span-3 sm:col-span-4 text-[8px] sm:text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">Kg</div>
                    <div className="col-span-3 sm:col-span-4 text-[8px] sm:text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">Reps</div>
                    <div className="col-span-2 text-[8px] sm:text-xs font-black uppercase tracking-widest text-text-secondary opacity-30 text-center">RIR</div>
                    <div className="col-span-3 sm:col-span-1"></div>
                 </div>

                 {setsRecords[activeExercise.exerciseId]?.map((record, idx) => (
                    <div 
                      key={idx} 
                      className={clsx(
                        "grid grid-cols-12 gap-2 sm:gap-4 items-center p-2.5 sm:p-3 rounded-xl sm:rounded-2xl transition-all duration-500",
                        record.isLogged ? "bg-primary/5 opacity-60 grayscale-[50%]" : "bg-surface-high/20 border border-white/[0.02]"
                      )}
                    >
                      <div className="col-span-1 flex justify-center">
                        <span className="font-display font-black text-text-secondary text-sm sm:text-base">{idx + 1}</span>
                      </div>
                      
                      <div className="col-span-3 sm:col-span-4">
                        <input 
                          type="number"
                          value={record.weight}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'weight', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-lg sm:rounded-xl py-2.5 sm:py-3 px-1 text-center text-xs sm:text-sm font-black text-primary focus:outline-none focus:border-primary/50 transition-colors"
                        />
                      </div>

                      <div className="col-span-3 sm:col-span-4">
                        <input 
                          type="number"
                          value={record.reps}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'reps', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-lg sm:rounded-xl py-2.5 sm:py-3 px-1 text-center text-xs sm:text-sm font-black text-text-main focus:outline-none focus:border-white/20 transition-colors"
                        />
                      </div>

                      <div className="col-span-2">
                        <select 
                          value={record.rir}
                          onChange={(e) => handleUpdateRecord(activeExercise.exerciseId, idx, 'rir', Number(e.target.value))}
                          disabled={record.isLogged}
                          className="w-full bg-background/50 border border-white/5 rounded-lg sm:rounded-xl py-2.5 sm:py-3 px-1 text-center text-[10px] sm:text-xs font-black text-primary focus:outline-none focus:border-primary/50 appearance-none"
                        >
                          {[0,1,2,3,4,5].map(v => <option key={v} value={v}>{v}</option>)}
                        </select>
                      </div>

                      <div className="col-span-3 sm:col-span-1 flex justify-end">
                        <button 
                          onClick={() => handleLogSet(activeExercise.exerciseId, idx)}
                          disabled={record.isLogged}
                          className={clsx(
                            "w-10 h-10 sm:w-12 sm:h-12 rounded-lg sm:rounded-xl flex items-center justify-center transition-all duration-300 shadow-lg",
                            record.isLogged 
                              ? "bg-green-500 text-background" 
                              : "bg-surface-high hover:bg-primary/20 text-text-secondary hover:text-primary border border-white/5"
                          )}
                        >
                          {record.isLogged ? <CheckCircle2 size={20} className="sm:size-[24px]" /> : <Save size={20} className="sm:size-[24px]" />}
                        </button>
                      </div>
                    </div>
                 ))}
              </div>
           </div>

           <div className="flex items-center justify-between gap-4 sm:gap-6">
              <Button 
                onClick={handlePrevExercise}
                variant="ghost" 
                className="flex-1 py-4 sm:py-5 rounded-xl sm:rounded-2xl text-[9px] sm:text-[10px] font-black uppercase tracking-widest text-text-secondary"
                disabled={activeExerciseIndex === 0}
              >
                Anterior
              </Button>
              {activeExerciseIndex < currentDay.exercises.length - 1 ? (
                <Button 
                  onClick={handleNextExercise}
                  variant="primary" 
                  className="flex-[2] py-4 sm:py-5 rounded-xl sm:rounded-2xl text-[9px] sm:text-[10px] font-black uppercase tracking-[0.2em] shadow-xl"
                >
                  Siguiente Ejercicio
                </Button>
              ) : (
                <Button 
                  onClick={handleFinish}
                  variant="primary" 
                  className="flex-[2] py-4 sm:py-5 rounded-xl sm:rounded-2xl text-[9px] sm:text-[10px] font-black uppercase tracking-[0.2em] shadow-[0_15px_30px_rgba(255,182,0,0.2)]"
                  disabled={!allExercisesCompleted || trackerLoading}
                >
                  {trackerLoading ? <Loader2 size={14} className="animate-spin" /> : "Finalizar Entrenamiento"}
                </Button>
              )}
           </div>
        </div>
      )}

      {/* MODAL DE SESIÓN PENDIENTE (RECUPERACIÓN) */}
      <Modal 
        isOpen={showResumeModal} 
        onClose={() => {}} 
        title="Entrenamiento en Progreso"
      >
        <div className="p-8 flex flex-col items-center text-center gap-6">
          <div className="w-16 h-16 bg-primary/10 rounded-2xl flex items-center justify-center text-primary mb-2">
            <History size={32} />
          </div>
          <div className="space-y-2">
            <h3 className="text-xl font-display font-black uppercase italic text-text-main">
              ¡Tienes una sesión pendiente!
            </h3>
            <p className="text-text-secondary text-sm font-medium max-w-xs mx-auto">
              Detectamos que no finalizaste tu último entrenamiento. ¿Deseas retomarlo desde donde quedaste o prefieres cancelarlo y empezar de cero?
            </p>
          </div>
          
          <div className="grid grid-cols-1 w-full gap-3 pt-4">
            <Button 
              onClick={handleResumeSession} 
              variant="primary" 
              className="py-4 rounded-xl text-[10px] font-black uppercase tracking-widest shadow-lg shadow-primary/20"
              icon={<Play size={14} />}
            >
              Continuar Entrenamiento
            </Button>
            <Button 
              onClick={handleCancelSession} 
              variant="ghost" 
              className="py-4 rounded-xl text-[10px] font-black uppercase tracking-widest text-error hover:bg-error/10 border-none"
              icon={<Trash2 size={14} />}
            >
              Cancelar y Eliminar
            </Button>
          </div>
        </div>
      </Modal>

      {/* MODAL DE OPCIONES (DURANTE EL ENTRENAMIENTO) */}
      <Modal 
        isOpen={showOptionsModal} 
        onClose={() => setShowOptionsModal(false)} 
        title="Gestión de Sesión"
      >
        <div className="p-8 flex flex-col gap-4">
          <div className="mb-4 text-center">
            <p className="text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary">
              ¿Qué deseas hacer con tu sesión actual?
            </p>
          </div>

          <Button 
            onClick={handleFinish} 
            variant="ghost" 
            className="w-full sm:max-w-[320px] sm:mx-auto py-6 text-[11px] font-black uppercase tracking-widest text-primary bg-primary/5 border border-primary/10 hover:bg-primary/10 hover:border-primary/20 shadow-[0_0_30px_rgba(137,172,255,0.05)]"
            icon={<StopCircle size={18} className="text-primary" />}
            disabled={trackerLoading}
          >
            Finalizar y Guardar
          </Button>

          <Button 
            onClick={handleCancelSession} 
            variant="ghost" 
            className="w-full sm:max-w-[320px] sm:mx-auto py-6 text-[11px] font-black uppercase tracking-widest text-error bg-error/5 border border-error/10 hover:bg-error/10 hover:border-error/20 shadow-[0_0_30px_rgba(255,113,108,0.05)]"
            icon={<Trash2 size={18} className="text-error" />}
            disabled={trackerLoading}
          >
            Cancelar Entrenamiento
          </Button>

          <Button 
            onClick={() => setShowOptionsModal(false)} 
            variant="primary"
            className="w-full sm:max-w-[320px] sm:mx-auto py-6 text-[11px] font-black uppercase tracking-widest mt-2"
          >
            Volver
          </Button>
        </div>
      </Modal>

      <VideoModal 
        isOpen={videoPlayer.isOpen}
        videoUrl={videoPlayer.url}
        title={videoPlayer.title}
        onClose={() => setVideoPlayer({ ...videoPlayer, isOpen: false })}
      />
    </div>
  );
};
