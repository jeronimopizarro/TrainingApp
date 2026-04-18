import React, { useState } from 'react';
import { 
  Library, 
  Plus, 
  Copy, 
  Eye, 
  Search,
  Dumbbell,
  ArrowRight,
  User,
  CheckCircle2,
  Loader2
} from 'lucide-react';
import { useBaseRoutines } from '../hooks/useBaseRoutines';
import { useMembers } from '../../members/hooks/useMembers';
import { Button } from '@/shared/components/Button';
import { Modal } from '@/shared/components/Modal';
import { Input } from '@/shared/components/Input';
import { useNavigate } from 'react-router-dom';

export const BaseRoutinesPage = () => {
  const { routines, isLoading, error, duplicateRoutine } = useBaseRoutines();
  const { members } = useMembers();
  const navigate = useNavigate();
  
  const [searchTerm, setSearchTerm] = useState('');
  const [isDuplicateModalOpen, setIsDuplicateModalOpen] = useState(false);
  const [selectedBaseRoutine, setSelectedBaseRoutine] = useState<any>(null);
  const [selectedMemberId, setSelectedMemberId] = useState<number | null>(null);
  const [newRoutineName, setNewRoutineName] = useState('');
  const [isDuplicating, setIsDuplicating] = useState(false);
  const [duplicateSuccess, setDuplicateSuccess] = useState(false);

  if (isLoading) return <div className="p-10 text-text-secondary animate-pulse font-display font-bold uppercase tracking-widest text-center">Cargando biblioteca de rutinas...</div>;

  const filteredRoutines = routines.filter(r => 
    r.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenDuplicate = (routine: any) => {
    setSelectedBaseRoutine(routine);
    setNewRoutineName(`Copia de ${routine.name}`);
    setIsDuplicateModalOpen(true);
    setDuplicateSuccess(false);
  };

  const handleDuplicate = async () => {
    if (!selectedMemberId || !newRoutineName) return;

    setIsDuplicating(true);
    const success = await duplicateRoutine(selectedBaseRoutine.id, selectedMemberId, newRoutineName);
    setIsDuplicating(false);

    if (success) {
      setDuplicateSuccess(true);
      setTimeout(() => {
        setIsDuplicateModalOpen(false);
        setSelectedMemberId(null);
      }, 2000);
    }
  };

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Biblioteca Técnica</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Rutinas <span className="text-primary-dark">Base</span>.
          </h1>
        </div>
        <div className="flex gap-4">
          <Button 
            variant="primary" 
            className="px-6 py-3 rounded-2xl text-[10px] font-black uppercase tracking-widest flex items-center gap-2"
            onClick={() => navigate('/trainer/routines/new-base')}
          >
            <Plus size={16} /> Crear Plantilla
          </Button>
        </div>
      </header>

      <div className="mb-10 relative max-w-xl">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary opacity-50" size={18} />
        <input 
          type="text"
          placeholder="Buscar plantillas (ej: Principiante)..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full bg-surface-low border border-white/5 rounded-2xl py-4 pl-12 pr-6 text-sm font-bold text-text-main focus:outline-none focus:border-primary/50 transition-all shadow-xl"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {filteredRoutines.map(routine => (
          <div key={routine.id} className="bg-surface-low p-8 rounded-[2.5rem] border border-white/5 hover:border-primary/30 transition-all group surface-lift">
            <div className="w-12 h-12 rounded-2xl bg-surface-high flex items-center justify-center text-primary mb-6 group-hover:scale-110 transition-transform shadow-lg">
              <Dumbbell size={24} />
            </div>
            
            <h3 className="text-xl font-display font-black text-text-main uppercase italic mb-2 leading-tight">{routine.name}</h3>
            <p className="text-[10px] text-text-secondary uppercase tracking-[0.2em] font-bold mb-8">Plantilla del Sistema</p>

            <div className="flex items-center gap-4 mt-auto">
              <Button 
                className="flex-1 justify-center gap-2 text-[10px] font-black uppercase tracking-widest text-text-secondary hover:text-primary transition-colors"
                onClick={() => navigate(`/routines/${routine.id}`)}
              >
                <Eye size={14} /> Detalles
              </Button>
              
            </div>
          </div>
        ))}
      </div>

      {/* DUPLICATE MODAL */}
      <Modal
        isOpen={isDuplicateModalOpen}
        onClose={() => !isDuplicating && setIsDuplicateModalOpen(false)}
        title="Asignar Plantilla a Socio"
      >
        {duplicateSuccess ? (
          <div className="flex flex-col items-center justify-center py-10 text-center animate-in zoom-in">
            <div className="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <CheckCircle2 size={32} className="text-primary" />
            </div>
            <h4 className="text-xl font-display font-black text-text-main uppercase italic">¡Rutina Asignada!</h4>
            <p className="text-sm text-text-secondary mt-2">La copia ha sido creada para el socio exitosamente.</p>
          </div>
        ) : (
          <div className="flex flex-col gap-6 py-4">
            <div className="space-y-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Nuevo Nombre de la Rutina</label>
              <Input 
                value={newRoutineName}
                onChange={(e) => setNewRoutineName(e.target.value)}
                placeholder="Ej: Rutina Personalizada - Juan"
              />
            </div>

            <div className="space-y-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Seleccionar Socio Destino</label>
              <div className="max-h-[300px] overflow-y-auto pr-2 space-y-2 custom-scrollbar">
                {members.map(member => (
                  <button
                    key={member.id}
                    onClick={() => setSelectedMemberId(member.id)}
                    className={`w-full flex items-center gap-4 p-4 rounded-xl border transition-all text-left ${
                      selectedMemberId === member.id 
                        ? 'bg-primary/10 border-primary shadow-lg shadow-primary/5' 
                        : 'bg-surface-high border-white/5 hover:border-white/20'
                    }`}
                  >
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center font-bold ${
                      selectedMemberId === member.id ? 'bg-primary text-background' : 'bg-background text-text-secondary'
                    }`}>
                      {member.firstName.charAt(0)}
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-bold text-text-main leading-tight">{member.firstName} {member.lastName}</p>
                      <p className="text-[10px] text-text-secondary font-medium uppercase tracking-tighter italic">DNI: {member.dni}</p>
                    </div>
                    {selectedMemberId === member.id && <CheckCircle2 size={16} className="text-primary" />}
                  </button>
                ))}
              </div>
            </div>

            <Button
              variant="primary"
              className="w-full py-4 mt-4 rounded-xl text-[10px] font-black uppercase tracking-widest justify-center gap-2"
              disabled={!selectedMemberId || isDuplicating}
              onClick={handleDuplicate}
            >
              {isDuplicating ? <Loader2 size={16} className="animate-spin" /> : <><Copy size={16} /> Crear Copia Asignada</>}
            </Button>
          </div>
        )}
      </Modal>
    </div>
  );
};
