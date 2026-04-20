import React, { useState, useEffect } from 'react';
import { 
  Dumbbell, 
  Plus, 
  Search, 
  Filter
} from 'lucide-react';
import { useExercises } from '../hooks/useExercises';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { VideoModal } from '@/shared/components/VideoModal';
import { Exercise } from '../types/exercise.types';
import { ExerciseCard } from '../components/ExerciseCard';
import { ExerciseForm } from '../components/ExerciseForm';
import { MuscleGroupFilter } from '../components/MuscleGroupFilter';

const INITIAL_FORM_STATE = {
  name: '',
  description: '',
  imageUrl: '',
  videoUrl: '',
  isBase: false, // Default to false for GYM_ADMIN
  muscleGroups: [] as { muscleGroupId: number, isPrimary: boolean }[]
};

export const ExercisesListPage = () => {
  const { 
    exercises, 
    muscleGroups, 
    isLoading, 
    error, 
    createExercise, 
    updateExercise, 
    deleteExercise, 
    refresh 
  } = useExercises();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedMuscleGroup, setSelectedMuscleGroup] = useState<number | undefined>(undefined);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);

  // Control de animación de entrada única para evitar que se repita al filtrar
  const [isFirstLoad, setIsFirstLoad] = useState(true);

  // Estado para el reproductor de video
  const [videoPlayer, setVideoPlayer] = useState<{ isOpen: boolean, url: string, title: string }>({
    isOpen: false,
    url: '',
    title: ''
  });

  // Marcar que la carga inicial terminó
  useEffect(() => {
    if (!isLoading && isFirstLoad) {
      setIsFirstLoad(false);
    }
  }, [isLoading, isFirstLoad]);

  const filteredExercises = exercises.filter(e => 
    e.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleMuscleGroupFilter = (id: number | undefined) => {
    setSelectedMuscleGroup(id);
    refresh(id);
  };

  const handleReset = () => {
    setFormData(INITIAL_FORM_STATE);
    setEditingId(null);
  };

  const handleEdit = (exercise: Exercise) => {
    setFormData({
      name: exercise.name,
      description: exercise.description,
      imageUrl: exercise.imageUrl || '',
      videoUrl: exercise.videoUrl || '',
      isBase: exercise.isBase,
      muscleGroups: exercise.muscleGroups.map(mg => ({ 
        muscleGroupId: mg.muscleGroupId, 
        isPrimary: mg.isPrimary 
      }))
    });
    setEditingId(exercise.id);
    setIsModalOpen(true);
  };

  const handleSubmit = async (data: any) => {
    let success = false;
    if (editingId) {
      success = await updateExercise(editingId, data);
    } else {
      success = await createExercise(data);
    }

    if (success) {
      setIsModalOpen(false);
      handleReset();
    }
  };

  const handleDelete = async () => {
    if (editingId && window.confirm('¿Estás seguro de eliminar este ejercicio?')) {
      const success = await deleteExercise(editingId);
      if (success) {
        setIsModalOpen(false);
        handleReset();
      }
    }
  };

  // Solo mostramos el spinner central en la carga inicial
  if (isLoading && isFirstLoad) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Cargando Biblioteca...</p>
    </div>
  );

  return (
    <div className={`pb-10 animate-in fade-in slide-in-from-bottom-4 duration-1000 ${isLoading ? 'opacity-40 grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-8 sm:mb-12">
        <div>
          <h2 className="text-[10px] sm:text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-2 sm:mb-3">Biblioteca</h2>
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic">
            Catálogo de <span className="text-primary-dark">Ejercicios</span>.
          </h1>
        </div>
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => { handleReset(); setIsModalOpen(true); }}
          className="w-full md:w-auto"
        >
          Nuevo Ejercicio
        </Button>
      </header>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6 mb-8 sm:mb-12">
        <StatCard 
          label="Total Biblioteca" 
          value={exercises.length} 
          icon={Dumbbell} 
          colorClass="text-primary" 
          description="Ejercicios registrados" 
        />
        <StatCard 
          label="Grupos Activos" 
          value={muscleGroups.length} 
          icon={Filter} 
          colorClass="text-green-400" 
          description="Zonas musculares" 
        />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <MuscleGroupFilter 
          muscleGroups={muscleGroups} 
          selectedId={selectedMuscleGroup} 
          onSelect={handleMuscleGroupFilter} 
        />

        <div className="relative flex-1 group w-full">
          <input 
            type="text"
            placeholder="BUSCAR EJERCICIO..."
            className="w-full bg-surface-low border border-white/[0.05] rounded-2xl py-5 px-8 text-[10px] font-black uppercase tracking-widest text-text-main focus:outline-none focus:border-primary/50 focus:ring-4 focus:ring-primary/5 transition-all shadow-inner"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {error && (
        <div className="mb-10 p-4 bg-error/10 border border-error/20 rounded-2xl text-error text-xs font-bold text-center">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {filteredExercises.map(exercise => (
          <ExerciseCard 
            key={exercise.id} 
            exercise={exercise} 
            onEdit={handleEdit} 
            muscleGroups={muscleGroups} 
            onPlayVideo={(url, title) => setVideoPlayer({ isOpen: true, url, title })}
          />
        ))}
      </div>

      {filteredExercises.length === 0 && !isLoading && (
        <div className="p-20 text-center bg-surface-low rounded-[1.5rem] border border-white/[0.03]">
          <Dumbbell size={48} className="mx-auto text-surface-high mb-4 opacity-20" />
          <p className="text-text-secondary font-bold italic opacity-40 text-sm">No se encontraron ejercicios</p>
        </div>
      )}

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title={editingId ? "Editar Ejercicio" : "Nuevo Ejercicio"}
      >
        <ExerciseForm 
          initialData={formData}
          muscleGroups={muscleGroups}
          isLoading={isLoading}
          isEditing={!!editingId}
          onSubmit={handleSubmit}
          onCancel={() => setIsModalOpen(false)}
          onDelete={handleDelete}
        />
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
