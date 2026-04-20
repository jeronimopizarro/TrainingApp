import React, { useState } from 'react';
import { 
  Save, 
  RotateCcw, 
  Trash2, 
  Image as ImageIcon, 
  PlayCircle 
} from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { CreateExerciseRequest, MuscleGroup } from '../types/exercise.types';

interface ExerciseFormProps {
  initialData: Omit<CreateExerciseRequest, 'muscleGroups'> & { muscleGroups: { muscleGroupId: number, isPrimary: boolean }[] };
  muscleGroups: MuscleGroup[];
  isLoading: boolean;
  isEditing: boolean;
  onSubmit: (data: any) => Promise<void>;
  onCancel: () => void;
  onDelete?: () => void;
}

export const ExerciseForm: React.FC<ExerciseFormProps> = ({
  initialData,
  muscleGroups,
  isLoading,
  isEditing,
  onSubmit,
  onCancel,
  onDelete
}) => {
  const [formData, setFormData] = useState(initialData);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleMuscleSelection = (muscleId: number) => {
    setFormData(prev => {
      const exists = prev.muscleGroups.find(m => m.muscleGroupId === muscleId);
      if (exists) {
        return { ...prev, muscleGroups: prev.muscleGroups.filter(m => m.muscleGroupId !== muscleId) };
      }
      return { 
        ...prev, 
        muscleGroups: [...prev.muscleGroups, { muscleGroupId: muscleId, isPrimary: prev.muscleGroups.length === 0 }] 
      };
    });
  };

  const setPrimaryMuscle = (muscleId: number) => {
    setFormData(prev => ({
      ...prev,
      muscleGroups: prev.muscleGroups.map(m => ({
        ...m,
        isPrimary: m.muscleGroupId === muscleId
      }))
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.muscleGroups.length === 0) {
      alert('Debes seleccionar al menos un grupo muscular.');
      return;
    }
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="space-y-6">
          <Input 
            label="Nombre del Ejercicio" 
            name="name" 
            value={formData.name} 
            onChange={handleInputChange} 
            placeholder="Ej. Press de Banca" 
            required 
          />
          <div className="space-y-2">
            <label className="text-[11px] uppercase tracking-widest text-text-secondary font-black ml-1">Descripción / Técnica</label>
            <textarea 
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              className="w-full bg-surface-high border border-white/[0.05] rounded-2xl py-4 px-4 text-base text-text-main focus:outline-none focus:border-primary/50 transition-all resize-none min-h-[120px]"
              placeholder="Explica la ejecución correcta..."
              required
            />
          </div>
          <Input 
            label="URL de Imagen (Opcional)" 
            name="imageUrl" 
            value={formData.imageUrl} 
            onChange={handleInputChange} 
            placeholder="https://..." 
            icon={<ImageIcon size={18} />}
          />
          <Input 
            label="URL de Video (Opcional)" 
            name="videoUrl" 
            value={formData.videoUrl} 
            onChange={handleInputChange} 
            placeholder="https://youtube.com/..." 
            icon={<PlayCircle size={18} />}
          />
        </div>

        <div className="space-y-6">
          <div className="space-y-4">
            <label className="text-[11px] uppercase tracking-widest text-text-secondary font-black ml-1">Grupos Musculares</label>
            <div className="grid grid-cols-2 gap-3 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
              {muscleGroups.length === 0 ? (
                <div className="col-span-2 p-8 text-center border border-dashed border-white/10 rounded-2xl">
                  <p className="text-[11px] text-text-secondary uppercase font-bold">No hay grupos musculares definidos en el sistema</p>
                </div>
              ) : (
                muscleGroups.map(mg => {
                  const isSelected = formData.muscleGroups.find(m => m.muscleGroupId === mg.id);
                  return (
                    <div
                      key={mg.id}
                      onClick={() => handleMuscleSelection(mg.id)}
                      className={`flex flex-col items-start p-4 rounded-xl border transition-all text-left cursor-pointer ${isSelected ? 'bg-primary/10 border-primary/30' : 'bg-surface-high border-white/5 hover:opacity-100'}`}
                    >
                      <span className={`text-sm font-bold ${isSelected ? 'text-primary' : 'text-text-secondary'}`}>{mg.name}</span>
                      {isSelected && (
                        <button
                          type="button"
                          onClick={(e) => { e.stopPropagation(); setPrimaryMuscle(mg.id); }}
                          className={`mt-3 text-[10px] uppercase tracking-tighter font-black px-3 py-1 rounded-full ${isSelected.isPrimary ? 'bg-primary text-white' : 'bg-white/5 text-text-secondary'}`}
                        >
                          {isSelected.isPrimary ? 'Primario' : 'Hacer Primario'}
                        </button>
                      )}
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="pt-8 border-t border-white/[0.03] flex flex-col sm:flex-row justify-between gap-4">
        {isEditing && onDelete && (
          <Button 
            type="button" 
            variant="secondary" 
            onClick={onDelete}
            className="text-error border-error/20 hover:bg-error/10"
            icon={<Trash2 size={18} />}
          >
            Eliminar
          </Button>
        )}
        <div className="flex flex-col sm:flex-row gap-4 ml-auto">
          <Button 
            type="button" 
            variant="secondary" 
            onClick={onCancel}
            icon={<RotateCcw size={18} />}
          >
            Cancelar
          </Button>
          <Button 
            type="submit" 
            isLoading={isLoading} 
            icon={<Save size={18} />}
          >
            {isEditing ? 'Guardar Cambios' : 'Crear Ejercicio'}
          </Button>
        </div>
      </div>
    </form>
  );
};
