import React, { useState, useEffect } from 'react';
import { 
  ChevronLeft, 
  Plus, 
  Trash2, 
  Dumbbell, 
  Search, 
  Save, 
  Loader2,
  Layout,
  Copy,
  Play
} from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { VideoModal } from '@/shared/components/VideoModal';
import { Exercise } from '@/features/exercises/types/exercise.types';
import { RoutineSummary, RoutineDetail } from '../types/routine.types';
import { clsx } from 'clsx';
import { routineService } from '../services/routine.service';

interface RoutineBuilderProps {
  initialData?: RoutineDetail | null;
  isEditMode?: boolean;
  isBaseMode?: boolean;
  isTrainerMode?: boolean;
  memberName?: string | null;
  exercises: Exercise[];
  baseRoutines: RoutineSummary[];
  loading?: boolean;
  onSave: (payload: any) => Promise<void>;
  onCancel: () => void;
}

export const RoutineBuilder: React.FC<RoutineBuilderProps> = ({
  initialData,
  isEditMode = false,
  isBaseMode = false,
  isTrainerMode = false,
  memberName,
  exercises,
  baseRoutines,
  loading: externalLoading = false,
  onSave,
  onCancel
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [isExerciseModalOpen, setIsExerciseModalOpen] = useState(false);
  const [isBaseModalOpen, setIsBaseModalOpen] = useState(false);
  const [activeDayIndex, setActiveDayIndex] = useState(0);

  // Video Player State
  const [videoPlayer, setVideoPlayer] = useState<{ isOpen: boolean, url: string, title: string }>({
    isOpen: false,
    url: '',
    title: ''
  });

  // Form State
  const [routineName, setRoutineName] = useState('');
  const [durationMonths, setDurationMonths] = useState<number | null>(3);
  const [days, setDays] = useState<any[]>([
    { dayName: 'Día 1', exercises: [] }
  ]);

  // Sincronizar con datos iniciales si existen (Modo Edición)
  useEffect(() => {
    if (initialData) {
      setRoutineName(initialData.name);
      const mappedDays = initialData.days.map(d => ({
        id: d.id,
        dayName: d.name,
        exercises: d.exercises.map(ex => ({
          id: ex.id,
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
      setRoutineName(
        isBaseMode ? 'Nueva Plantilla Base' : 
        memberName ? `Plan de Entrenamiento - ${memberName}` : 'Mi Rutina Personal'
      );
    }
  }, [initialData, isBaseMode, memberName]);

  const handleAddDay = () => {
    if (days.length >= 7) return;
    const nextNumber = days.length + 1;
    setDays([...days, { dayName: `Día ${nextNumber}`, exercises: [] }]);
    setActiveDayIndex(days.length);
  };

  const handleRemoveDay = (index: number) => {
    if (days.length === 1) return;
    const newDays = days.filter((_, i) => i !== index).map((day, i) => {
      const isDefaultName = day.dayName.match(/^Día \d+$/);
      return { 
        ...day, 
        dayName: isDefaultName ? `Día ${i + 1}` : day.dayName 
      };
    });
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
    const processedValue = (field === 'notes') ? value : (Number(value) || 0);
    newDays[dayIndex].exercises[exIndex] = { ...newDays[dayIndex].exercises[exIndex], [field]: processedValue };
    setDays(newDays);
  };

  const handleLoadBase = (baseDetail: RoutineDetail) => {
    const mappedDays = baseDetail.days.map((d, i) => ({
      dayName: d.name || `Día ${i + 1}`,
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
  };

  const handlePreSave = async () => {
    if (!routineName.trim()) return alert("Ingresa un nombre para la rutina");
    if (days.some(d => d.exercises.length === 0)) return alert("Todos los días deben tener ejercicios");

    const payload = {
      name: routineName,
      durationMonths: durationMonths,
      days: days.map(day => {
        const dayPayload: any = {
          dayName: day.dayName,
          exercises: day.exercises.map((ex: any) => {
            const exPayload: any = {
              exerciseId: ex.exerciseId,
              sets: Number(ex.sets),
              repsMin: Number(ex.repsMin),
              repsMax: Number(ex.repsMax),
              targetRIR: Number(ex.targetRIR),
              suggestedWeight: Number(ex.suggestedWeight),
              notes: ex.notes || ""
            };
            if (ex.id) exPayload.id = ex.id;
            return exPayload;
          })
        };
        if (day.id) dayPayload.id = day.id;
        return dayPayload;
      })
    };

    await onSave(payload);
  };

  const filteredExercises = exercises.filter(ex => 
    ex.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="animate-in fade-in duration-700">
      <header className="sticky top-0 z-30 bg-background/80 backdrop-blur-xl border-b border-white/[0.05] p-4 sm:p-6 flex flex-col sm:flex-row items-center justify-between gap-4">
         <div className="flex items-center gap-4 w-full sm:w-auto">
           <Button onClick={onCancel} variant="ghost" className="p-2 rounded-xl">
             <ChevronLeft />
           </Button>
           <div>
             <h1 className="text-sm font-black uppercase tracking-widest text-text-main leading-none mb-1">
                {isEditMode ? 'Editando Planificación' : isBaseMode ? 'Nueva Plantilla Base' : 'Constructor de Rutina'}
             </h1>
             <p className="text-[10px] font-bold text-primary uppercase tracking-tight italic">
                {isEditMode ? `ID: #${initialData?.id}` : isTrainerMode ? `Alumno: ${memberName || 'N/A'}` : 'Personalizado'}
             </p>
           </div>
         </div>

         <div className="flex gap-3 w-full sm:w-auto">
            {isTrainerMode && !isEditMode && (
                <Button 
                    variant="secondary" 
                    className="flex-1 sm:flex-none px-6 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest gap-2"
                    onClick={() => setIsBaseModalOpen(true)}
                >
                    <Layout size={14} /> Usar Plantilla
                </Button>
            )}
            <Button 
                onClick={handlePreSave} 
                variant="primary" 
                className="flex-1 sm:flex-none px-6 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest flex items-center justify-center gap-2 shadow-lg shadow-primary/20"
                disabled={externalLoading}
            >
                {externalLoading ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
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
            <div className="bg-surface-low rounded-[2.5rem] border border-white/[0.03] p-6 sm:p-8 shadow-2xl animate-in slide-in-from-right-4 duration-500">
            <div className="flex items-center justify-between mb-8 gap-4">
                <input 
                type="text"
                value={days[activeDayIndex].dayName}
                onChange={(e) => {
                    const newDays = [...days];
                    newDays[activeDayIndex].dayName = e.target.value;
                    setDays(newDays);
                }}
                className="bg-transparent text-xl sm:text-2xl font-display font-black text-text-main italic uppercase tracking-tight focus:outline-none border-b border-white/5 focus:border-primary/50 w-full max-w-[200px] sm:max-w-xs"
                />
                {days.length > 1 && (
                <button 
                    onClick={() => handleRemoveDay(activeDayIndex)}
                    className="p-2 text-error hover:bg-error/10 rounded-lg transition-colors shrink-0"
                >
                    <Trash2 size={18} />
                </button>
                )}
            </div>

            <div className="flex flex-col gap-4">
                {days[activeDayIndex].exercises.map((ex: any, exIdx: number) => {
                const exerciseData = exercises.find(e => e.id === ex.exerciseId);
                const currentImg = exerciseData?.imageUrl || ex.exerciseImageUrl;
                const currentName = exerciseData?.name || ex.exerciseName;
                const currentVideo = exerciseData?.videoUrl || ex.exerciseVideoUrl;

                return (
                    <div key={exIdx} className="bg-surface-high/20 border border-white/[0.02] rounded-2xl p-6 group hover:border-white/10 transition-all relative">
                    <button 
                        onClick={() => {
                            const newDays = [...days];
                            newDays[activeDayIndex].exercises = newDays[activeDayIndex].exercises.filter((_: any, i: number) => i !== exIdx);
                            setDays(newDays);
                        }}
                        className="absolute top-4 right-4 text-text-secondary opacity-30 hover:opacity-100 hover:text-error transition-all p-2 z-10 hidden sm:block"
                    >
                        <Trash2 size={18} />
                    </button>

                    <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6 mb-8 relative">
                        {/* Botón eliminar sobre imagen (Solo móvil, en el div flex) */}
                        <button 
                            onClick={() => {
                                const newDays = [...days];
                                newDays[activeDayIndex].exercises = newDays[activeDayIndex].exercises.filter((_: any, i: number) => i !== exIdx);
                                setDays(newDays);
                            }}
                            className="absolute top-2 right-2 z-30 p-3 bg-black/60 backdrop-blur-md text-white rounded-2xl sm:hidden border border-white/10 active:scale-95 transition-transform"
                        >
                            <Trash2 size={20} />
                        </button>

                        <div className="relative w-full sm:w-32 h-48 sm:h-32 rounded-2xl bg-background overflow-hidden flex items-center justify-center text-primary border border-white/5 shadow-2xl group/img shrink-0">
                            {currentImg ? (
                                <img src={currentImg} alt="" className="w-full h-full object-cover opacity-80 group-hover/img:opacity-100 transition-opacity" />
                            ) : (
                                <Dumbbell size={32} className="opacity-20" />
                            )}

                            {currentVideo && (
                              <button 
                                onClick={() => setVideoPlayer({ isOpen: true, url: currentVideo, title: currentName || '' })}
                                className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-100 sm:opacity-0 sm:group-hover/img:opacity-100 transition-opacity"
                              >
                                <Play size={32} className="text-primary fill-primary" />
                              </button>
                            )}
                        </div>
                        <div className="flex flex-col gap-1.5 min-w-0 text-center sm:text-left w-full">
                          <h4 className="font-black text-text-main uppercase italic text-xl sm:text-2xl tracking-tighter leading-tight break-words">{currentName || 'Cargando...'}</h4>
                          <p className="text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary italic">Técnica de Ejecución</p>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary px-1">Sets</label>
                            <input 
                            type="number"
                            value={ex.sets}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'sets', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary px-1">Min Reps</label>
                            <input 
                            type="number"
                            value={ex.repsMin}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'repsMin', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary px-1">Max Reps</label>
                            <input 
                            type="number"
                            value={ex.repsMax}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'repsMax', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-text-main focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary px-1">Peso (kg)</label>
                            <input 
                            type="number"
                            value={ex.suggestedWeight}
                            onChange={(e) => handleUpdateExercise(activeDayIndex, exIdx, 'suggestedWeight', Number(e.target.value))}
                            className="bg-background/50 border border-white/5 rounded-xl py-2 px-3 text-sm font-black text-primary focus:outline-none focus:border-primary/30"
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label className="text-[9px] font-black uppercase tracking-widest text-text-secondary px-1">RIR</label>
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
            <Input 
              placeholder="BUSCAR EJERCICIO..." 
              value={searchQuery} 
              onChange={(e) => setSearchQuery(e.target.value)} 
              className="py-5 text-[10px] font-black uppercase tracking-widest border-x-0 border-t-0 rounded-none bg-transparent" 
            />
          </div>
          <div className="grid grid-cols-1 gap-3 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
             {filteredExercises.map(ex => (
               <div key={ex.id} className="flex items-center gap-4 p-4 rounded-2xl bg-surface-high border border-white/5 hover:border-primary/40 transition-all text-left group relative">
                 <div className="w-14 h-14 rounded-xl overflow-hidden bg-background border border-white/5 flex-shrink-0">
                   {ex.imageUrl ? <img src={ex.imageUrl} alt={ex.name} className="w-full h-full object-cover group-hover:scale-110 transition-transform" /> : <div className="w-full h-full flex items-center justify-center text-primary/20"><Dumbbell size={20} /></div>}
                 </div>
                 <div className="flex-1 cursor-pointer" onClick={() => handleAddExerciseToDay(ex)}>
                   <h5 className="text-sm font-bold text-text-main group-hover:text-primary transition-colors uppercase italic">{ex.name}</h5>
                   <p className="text-[9px] text-text-secondary font-black uppercase tracking-tighter">{ex.isBase ? 'Ejercicio Base' : 'Personalizado'}</p>
                 </div>
                 <div className="flex items-center gap-2">
                    {ex.videoUrl && (
                      <button 
                        onClick={() => setVideoPlayer({ isOpen: true, url: ex.videoUrl || '', title: ex.name })}
                        className="p-2 bg-primary/10 hover:bg-primary/20 text-primary rounded-lg transition-all"
                        title="Ver Video"
                      >
                        <Play size={16} className="fill-primary" />
                      </button>
                    )}
                    <button 
                      onClick={() => handleAddExerciseToDay(ex)}
                      className="p-2 hover:bg-primary/10 text-primary opacity-0 group-hover:opacity-100 transition-all"
                    >
                      <Plus size={18} />
                    </button>
                 </div>
               </div>
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
                    <button 
                        key={base.id} 
                        onClick={async () => {
                            try {
                                const detail = await routineService.getById(base.id);
                                handleLoadBase(detail);
                            } catch (err) {
                                alert("Error al cargar la plantilla");
                            }
                        }} 
                        className="w-full flex items-center justify-between p-6 rounded-2xl bg-surface-high border border-white/5 hover:border-primary/50 transition-all text-left group"
                    >
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

      <VideoModal 
        isOpen={videoPlayer.isOpen}
        videoUrl={videoPlayer.url}
        title={videoPlayer.title}
        onClose={() => setVideoPlayer({ ...videoPlayer, isOpen: false })}
      />
    </div>
  );
};
