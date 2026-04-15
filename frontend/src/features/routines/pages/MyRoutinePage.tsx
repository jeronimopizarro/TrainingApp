import React, { useState } from 'react';
import { 
  ChevronRight, 
  Dumbbell, 
  Play, 
  Info,
  Calendar,
  User,
  Clock,
  ChevronDown,
  Loader2,
  AlertCircle
} from 'lucide-react';
import { useRoutine } from '@/features/routines/hooks/useRoutine';
import { authService } from '@/features/auth/services/auth.service';
import { Button } from '@/shared/components/Button';
import { VideoModal } from '@/shared/components/VideoModal';
import { useNavigate } from 'react-router-dom';
import { clsx } from 'clsx';

export const MyRoutinePage = () => {
  const userData = authService.getUserData();
  const { detail, loading, error } = useRoutine(userData?.userId);
  const [expandedDay, setExpandedDay] = useState<number | null>(0);
  const [videoData, setVideoData] = useState<{url: string, title: string} | null>(null);
  const navigate = useNavigate();

  if (loading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Cargando tu Plan...</p>
    </div>
  );

  if (error || !detail) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6 p-10 text-center">
      <AlertCircle className="w-16 h-16 text-error opacity-50" />
      <h2 className="text-2xl font-display font-black text-text-main">Sin Rutina Activa</h2>
      <p className="text-text-secondary max-w-md">No tienes una rutina asignada actualmente. Por favor, contacta con tu entrenador.</p>
      <Button onClick={() => navigate('/member/home')} variant="ghost" className="mt-4">Volver al Inicio</Button>
    </div>
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      {/* HEADER */}
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-8 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Tu Plan de Entrenamiento</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic uppercase">
            {detail.name}
          </h1>
          <div className="flex flex-wrap items-center gap-6 mt-6">
             <div className="flex items-center gap-2 text-text-secondary">
               <Calendar size={16} className="text-primary" />
               <span className="text-[10px] font-bold uppercase tracking-widest">Válida hasta: {new Date(detail.endDate).toLocaleDateString()}</span>
             </div>
             <div className="flex items-center gap-2 text-text-secondary">
               <Clock size={16} className="text-primary" />
               <span className="text-[10px] font-bold uppercase tracking-widest">{detail.days.length} Días / Semana</span>
             </div>
          </div>
        </div>
        
        <div className="flex items-center gap-3 bg-surface-low p-2 rounded-2xl border border-white/5">
           <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
             <User size={20} />
           </div>
           <div className="pr-4">
             <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary opacity-50">Entrenador</p>
             <p className="text-xs font-bold text-text-main">Asignado por el Staff</p>
           </div>
        </div>
      </header>

      {/* DAYS LIST */}
      <div className="flex flex-col gap-6 max-w-5xl">
        {detail.days.map((day, index) => (
          <div key={day.id} className="bg-surface-low rounded-[2rem] border border-white/[0.03] overflow-hidden transition-all duration-500 hover:border-white/10">
            <button 
              onClick={() => setExpandedDay(expandedDay === index ? null : index)}
              className="w-full flex items-center justify-between p-8 text-left hover:bg-white/[0.01] transition-colors"
            >
              <div className="flex items-center gap-6">
                <div className="w-14 h-14 bg-surface-high rounded-2xl flex items-center justify-center font-display font-black text-2xl text-primary border border-white/5 shadow-xl">
                   {index + 1}
                </div>
                <div>
                   <h3 className="text-2xl font-display font-black text-text-main italic uppercase tracking-tight">{day.name}</h3>
                   <p className="text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary opacity-40 mt-1">
                     {day.exercises.length} Ejercicios Programados
                   </p>
                </div>
              </div>
              <div className={clsx("transition-transform duration-500", expandedDay === index ? "rotate-180" : "rotate-0")}>
                <ChevronDown className="text-text-secondary" />
              </div>
            </button>

            <div className={clsx(
              "grid transition-all duration-500 ease-in-out",
              expandedDay === index ? "grid-rows-[1fr] opacity-100" : "grid-rows-[0fr] opacity-0"
            )}>
              <div className="overflow-hidden">
                <div className="p-8 pt-0 flex flex-col gap-4 border-t border-white/[0.02]">
                  {day.exercises.map((exercise) => (
                    <div key={exercise.exerciseId} className="group flex items-center gap-6 p-6 rounded-2xl bg-surface-high/20 hover:bg-surface-high/40 transition-all border border-white/[0.02] hover:border-primary/20">
                      <div className="w-16 h-16 rounded-xl overflow-hidden border border-white/5 bg-background flex-shrink-0 relative group">
                        {exercise.exerciseImageUrl ? (
                          <img src={exercise.exerciseImageUrl} alt={exercise.exerciseName} className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity" />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-primary/20">
                            <Dumbbell size={24} />
                          </div>
                        )}
                        {exercise.exerciseVideoUrl && (
                          <button 
                            onClick={(e) => { e.stopPropagation(); setVideoData({ url: exercise.exerciseVideoUrl, title: exercise.exerciseName }); }}
                            className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity"
                          >
                            <Play size={20} className="text-primary fill-primary" />
                          </button>
                        )}
                      </div>

                      <div className="flex-1 min-w-0">
                        <h4 className="text-xl font-display font-black text-text-main group-hover:text-primary transition-colors leading-none mb-6 uppercase italic tracking-tight">
                          {exercise.exerciseName}
                        </h4>
                        
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                           <div className="bg-surface-high/40 p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary opacity-50 mb-1">Series</p>
                             <p className="text-lg font-display font-black text-text-main italic">{exercise.sets}</p>
                           </div>
                           <div className="bg-surface-high/40 p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary opacity-50 mb-1">Rango Reps</p>
                             <p className="text-lg font-display font-black text-text-main italic">{exercise.repsMin}-{exercise.repsMax}</p>
                           </div>
                           <div className="bg-surface-high/40 p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary opacity-50 mb-1">Peso Objetivo</p>
                             <p className="text-lg font-display font-black text-primary italic">{exercise.suggestedWeight}kg</p>
                           </div>
                           <div className="bg-surface-high/40 p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary opacity-50 mb-1">Esfuerzo (RIR)</p>
                             <p className="text-lg font-display font-black text-blue-400 italic">{exercise.targetRIR}</p>
                           </div>
                        </div>
                      </div>

                      {exercise.notes && (
                        <div className="hidden lg:flex items-center gap-2 max-w-xs text-right">
                          <Info size={14} className="text-text-secondary opacity-30 flex-shrink-0" />
                          <p className="text-[10px] font-medium text-text-secondary italic line-clamp-2">
                            {exercise.notes}
                          </p>
                        </div>
                      )}

                      <div className="flex items-center justify-end">
                         <ChevronRight size={20} className="text-text-secondary opacity-20 group-hover:text-primary group-hover:opacity-100 transition-all" />
                      </div>
                    </div>
                  ))}

                  <div className="mt-8 flex justify-center">
                    <Button 
                      onClick={() => navigate(`/member/workout/${detail.id}/day/${day.id}`)}
                      variant="primary" 
                      className="px-10 py-4 rounded-xl text-xs font-black uppercase tracking-[0.2em] shadow-[0_10px_20px_rgba(255,182,0,0.1)]"
                    >
                      Entrenar este día
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <VideoModal 
        isOpen={!!videoData} 
        onClose={() => setVideoData(null)} 
        videoUrl={videoData?.url || ''} 
        title={videoData?.title || ''}
      />
    </div>
  );
};
