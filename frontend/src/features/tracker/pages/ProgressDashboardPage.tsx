import React, { useState } from 'react';
import { 
  Trophy, 
  TrendingUp, 
  Activity, 
  ChevronRight, 
  Dumbbell, 
  Search,
  Filter,
  Loader2,
  AlertCircle
} from 'lucide-react';
import { useProgress } from '../hooks/useProgress';
import { ExerciseProgressChart } from '../components/ExerciseProgressChart';
import { Button } from '@/shared/components/Button';
import { Modal } from '@/shared/components/Modal';
import { clsx } from 'clsx';

export const ProgressDashboardPage = () => {
  const { summary, loading, error } = useProgress();
  const [selectedExercise, setSelectedExercise] = useState<{id: number, name: string} | null>(null);
  const { exerciseProgress, loading: progressLoading, fetchExerciseProgress } = useProgress(selectedExercise?.id);
  const [searchTerm, setSearchTerm] = useState('');

  const handleOpenExercise = (id: number, name: string) => {
    setSelectedExercise({ id, name });
  };

  const filteredExercises = summary?.exercises.filter(ex => 
    ex.exerciseName.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  if (loading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Analizando Rendimiento...</p>
    </div>
  );

  if (error) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-10 text-center">
      <AlertCircle className="w-16 h-16 text-error opacity-50 mb-6" />
      <h2 className="text-2xl font-display font-black text-text-main uppercase italic mb-4">Error de Sincronización</h2>
      <p className="text-text-secondary max-w-sm mb-8">No hemos podido recuperar tus estadísticas de fuerza en este momento.</p>
      <Button onClick={() => window.location.reload()} variant="primary">Reintentar</Button>
    </div>
  );

  return (
    <div className="min-h-screen bg-background pb-20">
      {/* HEADER */}
      <header className="mb-12 px-8">
        <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Performance Hub</h2>
        <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
          Estadísticas de <span className="text-primary-dark">Fuerza</span>.
        </h1>
      </header>

      <div className="max-w-7xl mx-auto px-8">
        {/* SEARCH & FILTERS */}
        <div className="flex flex-col md:flex-row items-center gap-6 mb-8">
           <div className="flex-1 w-full relative">
             <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-text-secondary opacity-30" size={20} />
             <input 
               type="text" 
               placeholder="BUSCAR EJERCICIO..." 
               value={searchTerm}
               onChange={(e) => setSearchTerm(e.target.value)}
               className="w-full bg-surface-low border border-white/5 rounded-2xl py-5 pl-16 pr-6 text-xs font-black uppercase tracking-widest text-text-main focus:outline-none focus:border-primary/30 transition-all placeholder:text-text-secondary/20 shadow-inner"
             />
           </div>
        </div>

        {/* EXERCISES LIST */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
           {filteredExercises.length > 0 ? (
             filteredExercises.map((ex) => (
               <button 
                 key={ex.exerciseId}
                 onClick={() => handleOpenExercise(ex.exerciseId, ex.exerciseName)}
                 className="flex items-center justify-between p-6 bg-surface-low hover:bg-surface-high/40 rounded-3xl border border-white/[0.03] hover:border-primary/20 transition-all group"
               >
                 <div className="flex items-center gap-6">
                   <div className="w-14 h-14 bg-background rounded-2xl flex items-center justify-center text-primary group-hover:scale-110 transition-transform border border-white/5 shadow-inner">
                      <Dumbbell size={24} />
                   </div>
                   <div className="text-left">
                     <h5 className="text-sm font-black uppercase tracking-widest text-text-main mb-1 group-hover:text-primary transition-colors">{ex.exerciseName}</h5>
                     <div className="flex items-center gap-2">
                       <span className="text-[9px] font-black text-text-secondary opacity-40 uppercase tracking-tighter">Personal Record</span>
                       <span className="text-[10px] font-black text-primary uppercase">{ex.currentPersonalRecord} kg</span>
                     </div>
                   </div>
                 </div>
                 <ChevronRight className="text-text-secondary opacity-20 group-hover:opacity-100 group-hover:translate-x-1 transition-all" size={20} />
               </button>
             ))
           ) : (
             <div className="col-span-full py-20 flex flex-col items-center justify-center text-center opacity-30">
                <Dumbbell size={48} className="mb-4" />
                <p className="text-xs font-black uppercase tracking-widest">No se encontraron registros</p>
             </div>
           )}
        </div>
      </div>

      {/* PROGRESS MODAL */}
      <Modal 
        isOpen={!!selectedExercise} 
        onClose={() => setSelectedExercise(null)}
        title={selectedExercise?.name || 'Progreso de Ejercicio'}
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
                
                <div className="mt-10 grid grid-cols-2 gap-4">
                   <div className="bg-surface-high/20 p-6 rounded-3xl border border-white/5">
                      <p className="text-[9px] font-black text-text-secondary uppercase tracking-[0.2em] mb-2 opacity-50">Máximo Histórico</p>
                      <p className="text-2xl font-display font-black text-primary italic uppercase tracking-tighter">
                         {Math.max(...exerciseProgress.dataPoints.map(d => d.e1rm), 0).toFixed(1)} kg
                      </p>
                   </div>
                   <div className="bg-surface-high/20 p-6 rounded-3xl border border-white/5">
                      <p className="text-[9px] font-black text-text-secondary uppercase tracking-[0.2em] mb-2 opacity-50">Sesiones Registradas</p>
                      <p className="text-2xl font-display font-black text-text-main italic uppercase tracking-tighter">
                         {exerciseProgress.dataPoints.length}
                      </p>
                   </div>
                </div>
                
                <div className="mt-8 flex justify-center">
                   <Button onClick={() => setSelectedExercise(null)} variant="ghost" className="text-[10px] font-black uppercase tracking-widest text-text-secondary">Cerrar Análisis</Button>
                </div>
             </div>
           ) : null}
        </div>
      </Modal>
    </div>
  );
};
