import React, { useState } from 'react';
import { 
  Dumbbell, 
  Search,
  ChevronRight,
  Loader2,
  AlertCircle,
  TrendingUp,
  ChevronLeft
} from 'lucide-react';
import { useProgress } from '../hooks/useProgress';
import { ExerciseProgressChart } from '../components/ExerciseProgressChart';
import { Button } from '@/shared/components/Button';
import { Modal } from '@/shared/components/Modal';
import { useNavigate } from 'react-router-dom';

interface ProgressDashboardProps {
  memberId?: number;
  showTitle?: boolean;
}

export const ProgressDashboard = ({ memberId, showTitle = true }: ProgressDashboardProps) => {
  const navigate = useNavigate();
  const { summary, loading, error } = useProgress(undefined, memberId);
  const [selectedExercise, setSelectedExercise] = useState<{id: number, name: string} | null>(null);
  const { exerciseProgress, loading: progressLoading } = useProgress(selectedExercise?.id, memberId);
  const [searchTerm, setSearchTerm] = useState('');

  const handleOpenExercise = (id: number, name: string) => {
    setSelectedExercise({ id, name });
  };

  const filteredExercises = summary?.exercises.filter(ex => 
    ex.exerciseName.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  if (loading) return (
    <div className="flex flex-col items-center justify-center py-20 gap-6">
      <Loader2 className="w-10 h-10 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px] text-text-secondary">Analizando Rendimiento...</p>
    </div>
  );

  if (error) return (
    <div className="flex flex-col items-center justify-center py-20 p-10 text-center">
      <AlertCircle className="w-12 h-12 text-error opacity-50 mb-6" />
      <h2 className="text-xl font-display font-black text-text-main uppercase italic mb-4">Sin datos de progreso</h2>
      <p className="text-text-secondary text-sm max-w-sm mb-8">No hemos podido recuperar registros de entrenamiento para este perfil.</p>
    </div>
  );

  return (
    <div className="w-full">
      {/* HEADER OPCIONAL */}
      {showTitle && (
        <header className="mb-10">
            <div className="flex items-center gap-2 sm:gap-4 mb-2 sm:mb-3">
              <button 
                onClick={() => navigate(-1)} 
                className="p-1.5 sm:p-2 hover:bg-surface-low rounded-xl transition-colors text-text-secondary"
              >
                <ChevronLeft size={20} />
              </button>
              <h2 className="text-[10px] sm:text-xs font-sans font-bold text-primary uppercase tracking-[0.4em]">
                DESEMPEÑO
              </h2>
            </div>
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic uppercase">
              Estadísticas de <span className="text-primary-dark">Fuerza</span>.
            </h1>
        </header>
      )}

      {/* SEARCH */}
      <div className="relative mb-10">
        <input 
            type="text" 
            placeholder="BUSCAR EJERCICIO..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-surface-high/30 border border-white/5 rounded-2xl py-5 px-8 text-[10px] font-black uppercase tracking-widest text-text-main focus:outline-none focus:border-primary/30 transition-all placeholder:text-text-secondary/20 shadow-inner"
        />
      </div>

      {/* EXERCISES LIST */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredExercises.length > 0 ? (
            filteredExercises.map((ex) => (
            <button 
                key={ex.exerciseId}
                onClick={() => handleOpenExercise(ex.exerciseId, ex.exerciseName)}
                className="flex items-center justify-between p-5 bg-surface-high/20 hover:bg-surface-high/40 rounded-3xl border border-white/[0.03] hover:border-primary/20 transition-all group"
            >
                <div className="flex items-center gap-5">
                <div className="w-14 h-14 bg-background rounded-2xl flex items-center justify-center text-primary group-hover:scale-110 transition-transform border border-white/5 shadow-inner overflow-hidden">
                    {ex.exerciseImageUrl ? (
                      <img src={ex.exerciseImageUrl} alt={ex.exerciseName} className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity" />
                    ) : (
                      <Dumbbell size={24} className="opacity-20" />
                    )}
                </div>
                <div className="text-left">
                    <h5 className="text-xs font-black uppercase italic text-text-main mb-1 group-hover:text-primary transition-colors tracking-tight">{ex.exerciseName}</h5>
                    <div className="flex items-center gap-2">
                    <span className="text-[8px] font-black text-text-secondary uppercase tracking-tighter">PR</span>
                    <span className="text-[10px] font-black text-primary uppercase">{ex.currentPersonalRecord} kg</span>
                    </div>
                </div>
                </div>
                <ChevronRight className="text-text-secondary opacity-20 group-hover:opacity-100 group-hover:translate-x-1 transition-all" size={16} />
            </button>
            ))
        ) : (
            <div className="col-span-full py-20 flex flex-col items-center justify-center text-center opacity-30">
            <TrendingUp size={48} className="mb-4" />
            <p className="text-[10px] font-black uppercase tracking-widest">No hay registros de fuerza aún</p>
            </div>
        )}
      </div>

      {/* PROGRESS MODAL */}
      <Modal 
        isOpen={!!selectedExercise} 
        onClose={() => setSelectedExercise(null)}
        title={selectedExercise?.name || ''}
      >
        <div className="p-2">
           {progressLoading ? (
             <div className="h-80 flex flex-col items-center justify-center gap-4">
                <Loader2 className="animate-spin text-primary" size={32} />
                <p className="text-[10px] font-black uppercase tracking-[0.3em] text-text-secondary">Generando Gráfica...</p>
             </div>
           ) : exerciseProgress ? (
             <div className="animate-in zoom-in duration-500">
                <ExerciseProgressChart 
                  data={exerciseProgress.dataPoints} 
                  exerciseName={exerciseProgress.exerciseName} 
                />
                
                <div className="mt-8 sm:mt-10 grid grid-cols-1 sm:grid-cols-2 gap-4">
                   <div className="bg-surface-high/20 p-5 sm:p-6 rounded-2xl sm:rounded-3xl border border-white/5 shadow-inner">
                      <p className="text-[8px] sm:text-[9px] font-black text-text-secondary uppercase tracking-[0.2em] mb-2">Máximo Histórico</p>
                      <p className="text-xl sm:text-2xl font-display font-black text-primary italic uppercase tracking-tighter">
                         {Math.max(...exerciseProgress.dataPoints.map(d => d.e1rm), 0).toFixed(1)} kg
                      </p>
                   </div>
                   <div className="bg-surface-high/20 p-5 sm:p-6 rounded-2xl sm:rounded-3xl border border-white/5 shadow-inner">
                      <p className="text-[8px] sm:text-[9px] font-black text-text-secondary uppercase tracking-[0.2em] mb-2">Registros Totales</p>
                      <p className="text-xl sm:text-2xl font-display font-black text-text-main italic uppercase tracking-tighter">
                         {exerciseProgress.dataPoints.length}
                      </p>
                   </div>
                </div>
             </div>
           ) : null}
        </div>
      </Modal>
    </div>
  );
};
