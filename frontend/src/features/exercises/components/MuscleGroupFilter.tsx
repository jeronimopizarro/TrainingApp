import React from 'react';
import { Button } from '@/shared/components/Button';
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
    <div className="flex items-center gap-2 overflow-x-auto pb-2 custom-scrollbar bg-surface-low p-1.5 rounded-2xl w-fit border border-white/[0.03]">
      <Button 
        onClick={() => onSelect(undefined)}
        variant={!selectedId ? 'primary' : 'ghost'}
        className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all shrink-0 ${!selectedId ? '' : 'text-text-secondary hover:text-text-main'}`}
      >
        Todos
      </Button>
      {muscleGroups.map(mg => (
        <Button 
          key={mg.id}
          onClick={() => onSelect(mg.id)}
          variant={selectedId === mg.id ? 'primary' : 'ghost'}
          className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all shrink-0 ${selectedId === mg.id ? '' : 'text-text-secondary hover:text-text-main'}`}
        >
          {mg.name}
        </Button>
      ))}
    </div>
  );
};

