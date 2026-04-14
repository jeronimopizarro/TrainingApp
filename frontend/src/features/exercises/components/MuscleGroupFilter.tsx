import React from 'react';
import { MuscleGroup } from '../types/exercise.types';

interface MuscleGroupFilterProps {
  muscleGroups: MuscleGroup[];
  selectedId: number | undefined;
  onSelect: (id: number | undefined) => void;
}

export const MuscleGroupFilter: React.FC<MuscleGroupFilterProps> = ({
  muscleGroups,
  selectedId,
  onSelect
}) => {
  return (
    <div className="flex items-center gap-2 overflow-x-auto pb-2 custom-scrollbar">
      <button 
        onClick={() => onSelect(undefined)}
        className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all shrink-0 ${!selectedId ? 'bg-primary text-white shadow-lg shadow-primary/20' : 'bg-surface-low text-text-secondary hover:text-text-main border border-white/5'}`}
      >
        Todos
      </button>
      {muscleGroups.map(mg => (
        <button 
          key={mg.id}
          onClick={() => onSelect(mg.id)}
          className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all shrink-0 ${selectedId === mg.id ? 'bg-primary-dark text-white shadow-lg shadow-primary/20' : 'bg-surface-low text-text-secondary hover:text-text-main border border-white/5'}`}
        >
          {mg.name}
        </button>
      ))}
    </div>
  );
};
