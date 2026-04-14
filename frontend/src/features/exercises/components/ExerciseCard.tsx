import React from 'react';
import { Dumbbell, Edit3, PlayCircle } from 'lucide-react';
import { Exercise, MuscleGroup } from '../types/exercise.types';

interface ExerciseCardProps {
  exercise: Exercise;
  muscleGroups: MuscleGroup[];
  onEdit: (exercise: Exercise) => void;
  onPlayVideo: (videoUrl: string, title: string) => void;
}

export const ExerciseCard: React.FC<ExerciseCardProps> = ({ exercise, muscleGroups, onEdit, onPlayVideo }) => {
  const primaryMuscle = exercise.muscleGroups.find(mg => mg.isPrimary);
  const muscleName = muscleGroups.find(m => m.id === primaryMuscle?.muscleGroupId)?.name || 'General';

  return (
    <div className="group bg-surface-low border border-white/[0.03] rounded-[1.5rem] overflow-hidden hover:bg-surface-med/50 transition-all shadow-xl flex flex-col">
      <div className="relative h-48 bg-surface-high overflow-hidden">
        {exercise.imageUrl ? (
          <img 
            src={exercise.imageUrl} 
            alt={exercise.name} 
            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" 
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-white/5">
            <Dumbbell size={64} />
          </div>
        )}
        <div className="absolute top-4 left-4">
          <span className="px-3 py-1 bg-primary/90 backdrop-blur-md text-white text-[10px] font-black uppercase tracking-widest rounded-full shadow-lg">
            {muscleName}
          </span>
        </div>
        {exercise.videoUrl && (
          <div className="absolute bottom-4 right-4">
            <button 
              onClick={(e) => { e.stopPropagation(); onPlayVideo(exercise.videoUrl!, exercise.name); }}
              className="p-3 bg-primary/90 hover:bg-primary backdrop-blur-md rounded-full text-white shadow-xl hover:scale-110 transition-all active:scale-95"
            >
              <PlayCircle size={24} />
            </button>
          </div>
        )}
      </div>

      <div className="p-6 flex-1 flex flex-col">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-display font-black text-text-main uppercase italic tracking-tight">
            {exercise.name}
          </h3>
          <button 
            onClick={() => onEdit(exercise)}
            className="p-2 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-xl transition-all"
          >
            <Edit3 size={18} />
          </button>
        </div>
        <p className="text-xs text-text-secondary font-medium opacity-60 line-clamp-2 mb-6">
          {exercise.description}
        </p>
        
        <div className="mt-auto flex items-center gap-2">
          <div className="flex -space-x-2">
            {exercise.muscleGroups.slice(0, 3).map((mg, i) => (
              <div 
                key={i} 
                className="w-6 h-6 rounded-full bg-surface-high border border-white/10 flex items-center justify-center" 
                title={mg.isPrimary ? 'Primario' : 'Secundario'}
              >
                <div className={`w-1.5 h-1.5 rounded-full ${mg.isPrimary ? 'bg-primary' : 'bg-text-secondary opacity-30'}`} />
              </div>
            ))}
          </div>
          <span className="text-[10px] font-bold text-text-secondary opacity-40 uppercase tracking-widest">
            {exercise.muscleGroups.length} Grupos
          </span>
        </div>
      </div>
    </div>
  );
};
