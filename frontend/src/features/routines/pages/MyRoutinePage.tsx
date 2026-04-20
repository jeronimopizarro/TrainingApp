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
  AlertCircle,
  Trash2,
  Trophy,
  Edit3,
  ChevronLeft
} from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import { useRoutine } from '@/features/routines/hooks/useRoutine';
import { routineService } from '@/features/routines/services/routine.service';
import { authService } from '@/features/auth/services/auth.service';
import { Button } from '@/shared/components/Button';
import { Modal } from '@/shared/components/Modal';
import { VideoModal } from '@/shared/components/VideoModal';
import { clsx } from 'clsx';

export const MyRoutinePage = () => {
  const { id } = useParams();
  const userData = authService.getUserData();
  const userRole = authService.getUserRole();
  const navigate = useNavigate();

  // Si hay ID en la URL, usamos ese. Si no, usamos el del socio logueado para ver "Mi Rutina".
  const routineId = id ? Number(id) : undefined;
  const { detail, loading, error } = useRoutine(routineId ? undefined : userData?.userId, routineId);
  
  const [expandedDay, setExpandedDay] = useState<number | null>(null);
  const [videoData, setVideoData] = useState<{url: string, title: string} | null>(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const isStaff = userRole === 'TRAINER' || userRole === 'GYM_ADMIN';
  const isBaseRoutine = detail?.isBase;

  if (loading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Consultando Planificación...</p>
    </div>
  );

  if (error || !detail) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6 p-10 text-center">
      <AlertCircle className="w-16 h-16 text-error opacity-50" />
      <h2 className="text-2xl font-display font-black text-text-main">No se encontró la rutina</h2>
      <p className="text-text-secondary max-w-md">La planificación solicitada no está disponible o ha sido eliminada.</p>
      <Button onClick={() => navigate(-1)} variant="ghost" className="mt-4">Regresar</Button>
    </div>
  );

  const handleDeleteRoutine = async () => {
    if (!detail) return;
    setDeleteLoading(true);
    try {
      await routineService.delete(detail.id);
      setIsDeleteModalOpen(false);
      navigate(isStaff ? '/trainer/routines/bases' : '/member/dashboard');
    } catch (err) {
      alert("Error al eliminar la rutina");
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div className="pb-10 animate-in fade-in duration-700">
      {/* HEADER */}
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 sm:gap-8 mb-8 sm:mb-12 px-4 sm:px-6">
        <div>
          <div className="flex items-center gap-2 sm:gap-4 mb-2 sm:mb-3">
             <button onClick={() => navigate(-1)} className="p-1.5 sm:p-2 hover:bg-surface-low rounded-xl transition-colors text-text-secondary">
               <ChevronLeft size={20} />
             </button>
             <h2 className="text-[10px] sm:text-xs font-sans font-bold text-primary uppercase tracking-[0.4em]">
               {isBaseRoutine ? 'Plantilla Técnica' : 'Plan de Entrenamiento'}
             </h2>
          </div>
          
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic uppercase">
            {detail.name.toLowerCase().endsWith('personal') 
              ? <>
                  {detail.name.substring(0, detail.name.toLowerCase().lastIndexOf('personal'))}
                  <span className="text-primary-dark">Personal</span>
                </>
              : detail.name
            }.
          </h1>


          <div className="flex flex-wrap items-center gap-4 sm:gap-6 mt-4 sm:mt-6">
             {!isBaseRoutine && (
                <div className="flex items-center gap-2 text-text-secondary">
                  <Calendar size={14} className="text-primary sm:size-[16px]" />
                  <span className="text-[8px] sm:text-[10px] font-bold uppercase tracking-widest">
                    Vence: {detail.endDate ? new Date(detail.endDate).toLocaleDateString() : 'Indefinida'}
                  </span>
                </div>
             )}
             <div className="flex items-center gap-2 text-text-secondary">
               <Clock size={14} className="text-primary sm:size-[16px]" />
               <span className="text-[8px] sm:text-[10px] font-bold uppercase tracking-widest">{detail.days.length} Días / Semana</span>
             </div>
          </div>
        </div>
        
        <div className="flex flex-col sm:flex-row items-center gap-3 sm:gap-4">
          {(isStaff || detail.createdByUserId === userData?.userId) && (
             <Button 
                onClick={() => navigate(isStaff ? `/trainer/routines/builder?editId=${detail.id}` : `/member/routine/builder?editId=${detail.id}`)}
                variant="secondary" 
                icon={<Edit3 size={16} />}
                className="rounded-2xl w-full sm:w-auto text-xs"
             >
               Editar
             </Button>
          )}
          <Button 
            onClick={() => setIsDeleteModalOpen(true)}
            variant="primary" 
            icon={<Trash2 size={16} />}
            className="bg-error hover:bg-error-dark border-none rounded-2xl w-full sm:w-auto text-xs"
          >
            Eliminar
          </Button>
        </div>
      </header>

      {/* DAYS LIST */}
      <div className="flex flex-col gap-4 sm:gap-6 max-w-5xl mx-auto px-4 sm:px-6">
        {detail.days.map((day, index) => (
          <div key={day.id} className="bg-surface-low rounded-[1.5rem] sm:rounded-[2rem] border border-white/[0.03] overflow-hidden transition-all duration-500">
            <button 
              onClick={() => setExpandedDay(expandedDay === index ? null : index)}
              className="w-full flex items-center justify-between p-5 sm:p-8 text-left hover:bg-white/[0.01] transition-colors"
            >
              <div className="flex items-center gap-4 sm:gap-6">
                <div className="w-10 h-10 sm:w-14 sm:h-14 bg-surface-high rounded-xl sm:rounded-2xl flex items-center justify-center font-display font-black text-xl sm:text-2xl text-primary border border-white/5 shadow-xl">
                   {index + 1}
                </div>
                <div>
                   <h3 className="text-xl sm:text-2xl font-display font-black text-text-main italic uppercase tracking-tight">{day.name}</h3>
                   <p className="text-[8px] sm:text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary mt-1">
                     {day.exercises.length} Ejercicios
                   </p>
                </div>
              </div>
              <div className={clsx("transition-transform duration-500", expandedDay === index ? "rotate-180" : "rotate-0")}>
                <ChevronDown className="text-text-secondary" size={20} />
              </div>
            </button>

            <div className={clsx(
              "grid transition-all duration-500 ease-in-out",
              expandedDay === index ? "grid-rows-[1fr] opacity-100" : "grid-rows-[0fr] opacity-0"
            )}>
              <div className="overflow-hidden">
                <div className="p-4 sm:p-8 pt-0 flex flex-col gap-3 sm:gap-4 border-t border-white/[0.02]">
                  {day.exercises.map((exercise) => (
                    <div key={exercise.exerciseId} className="group flex flex-col sm:flex-row sm:items-center gap-4 sm:gap-6 p-4 sm:p-6 rounded-2xl bg-surface-high/20 hover:bg-surface-high/40 transition-all border border-white/[0.02] hover:border-primary/20 shadow-inner">
                      <div className="flex items-center gap-4 sm:gap-0 sm:block">
                        <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-xl overflow-hidden border border-white/5 bg-background flex-shrink-0 relative group/img">
                          {exercise.exerciseImageUrl ? (
                            <img src={exercise.exerciseImageUrl} alt={exercise.exerciseName} className="w-full h-full object-cover opacity-80 group-hover/img:opacity-100 transition-opacity" />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center text-primary/20">
                              <Dumbbell size={24} />
                            </div>
                          )}
                          {exercise.exerciseVideoUrl && (
                            <button 
                              onClick={(e) => { e.stopPropagation(); setVideoData({ url: exercise.exerciseVideoUrl, title: exercise.exerciseName }); }}
                              className="absolute inset-0 flex items-center justify-center bg-black/50 sm:bg-black/40 opacity-100 sm:opacity-0 sm:group-hover/img:opacity-100 transition-opacity"
                            >
                              <Play size={20} className="text-primary fill-primary sm:size-[24px]" />
                            </button>
                          )}
                        </div>
                        <div className="sm:hidden">
                          <h4 className="text-sm font-display font-black text-text-main group-hover:text-primary transition-colors leading-tight uppercase italic tracking-tight">
                            {exercise.exerciseName}
                          </h4>
                        </div>
                      </div>

                      <div className="flex-1 min-w-0">
                        <h4 className="hidden sm:block text-xl font-display font-black text-text-main group-hover:text-primary transition-colors leading-none mb-6 uppercase italic tracking-tight">
                          {exercise.exerciseName}
                        </h4>
                        
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 sm:gap-4">
                           <div className="bg-surface-high/40 p-2 sm:p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[7px] sm:text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary mb-1">Series</p>
                             <p className="text-base sm:text-lg font-display font-black text-text-main italic">{exercise.sets}</p>
                           </div>
                           <div className="bg-surface-high/40 p-2 sm:p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[7px] sm:text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary mb-1">Reps</p>
                             <p className="text-base sm:text-lg font-display font-black text-text-main italic">{exercise.repsMin}-{exercise.repsMax}</p>
                           </div>
                           <div className="bg-surface-high/40 p-2 sm:p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[7px] sm:text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary mb-1">Peso</p>
                             <p className="text-base sm:text-lg font-display font-black text-primary italic">{exercise.suggestedWeight}kg</p>
                           </div>
                           <div className="bg-surface-high/40 p-2 sm:p-3 rounded-xl border border-white/[0.03]">
                             <p className="text-[7px] sm:text-[9px] font-black uppercase tracking-[0.2em] text-text-secondary mb-1">Esfuerzo</p>
                             <p className="text-base sm:text-lg font-display font-black text-primary italic">{exercise.targetRIR} RIR</p>
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
                    </div>
                  ))}

                  {/* SOLO EL SOCIO PUEDE VER EL BOTÓN DE ENTRENAR Y SOLO EN RUTINAS NO-BASE */}
                  {!isStaff && !isBaseRoutine && (
                    <div className="mt-8 flex justify-center">
                        <Button 
                        onClick={() => navigate(`/member/workout/${detail.id}/day/${day.id}`)}
                        variant="primary" 
                        >
                        Entrenar este día
                        </Button>
                    </div>
                  )}
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

      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => !deleteLoading && setIsDeleteModalOpen(false)}
        title="Confirmar Eliminación"
      >
        <div className="flex flex-col items-center justify-center py-6 text-center">
           <div className="w-16 h-16 bg-error/10 rounded-full flex items-center justify-center text-error mb-6">
             <AlertCircle size={32} />
           </div>
           <h4 className="text-xl font-display font-black text-text-main uppercase italic mb-3">¿Eliminar esta rutina?</h4>
           <p className="text-xs text-text-secondary font-medium leading-relaxed mb-8 max-w-[280px]">
             Esta acción no se puede deshacer. {isBaseRoutine ? 'Se eliminará la plantilla de la biblioteca.' : 'Se perderá todo el progreso actual de esta planificación.'}
           </p>
           
           <div className="flex gap-3 w-full">
             <Button 
               onClick={() => setIsDeleteModalOpen(false)} 
               variant="ghost" 
               className="flex-1"
               disabled={deleteLoading}
             >
               Cancelar
             </Button>
             <Button 
               onClick={handleDeleteRoutine} 
               variant="primary" 
               className="flex-1 bg-error hover:bg-error-dark border-none shadow-error/20"
               isLoading={deleteLoading}
             >
               Sí, Eliminar
             </Button>
           </div>
        </div>
      </Modal>
    </div>
  );
};
