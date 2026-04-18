import React, { useState } from 'react';
import { 
  History, 
  User, 
  TrendingUp, 
  Search,
  ChevronRight,
  Calendar,
  Activity,
  Eye
} from 'lucide-react';
import { useTrainerRoutines } from '../hooks/useTrainerRoutines';
import { Button } from '@/shared/components/Button';
import { ProgressDashboard } from '../../tracker/components/ProgressDashboard';
import { useNavigate } from 'react-router-dom';

export const MyCreatedRoutinesPage = () => {
  const { routines, isLoading, error } = useTrainerRoutines();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRoutine, setSelectedRoutine] = useState<any>(null);
  const navigate = useNavigate();

  if (isLoading) return <div className="p-10 text-text-secondary animate-pulse font-display font-bold uppercase tracking-widest text-center">Cargando tus rutinas...</div>;

  const filteredRoutines = routines.filter(r => 
    r.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
    r.memberName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Seguimiento de Alumnos</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Mis Rutinas <span className="text-primary-dark">Asignadas</span>.
          </h1>
        </div>
      </header>

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-10">
        <div className="xl:col-span-1 space-y-6">
          <div className="relative mb-8">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary opacity-50" size={18} />
            <input 
              type="text"
              placeholder="Buscar por rutina o socio..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-surface-low border border-white/5 rounded-2xl py-4 pl-12 pr-6 text-[10px] font-black uppercase tracking-widest text-text-main focus:outline-none focus:border-primary/50 transition-all shadow-lg"
            />
          </div>

          <div className="space-y-4 max-h-[700px] overflow-y-auto pr-2 custom-scrollbar">
            {filteredRoutines.length === 0 ? (
               <div className="py-12 px-6 bg-surface-low/50 border border-dashed border-surface-med/30 rounded-[2rem] text-center">
                  <History size={32} className="mx-auto text-surface-med mb-4 opacity-20" />
                  <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary/50">No has creado rutinas aún</p>
               </div>
            ) : (
              filteredRoutines.map(routine => (
                <button
                  key={routine.id}
                  onClick={() => setSelectedRoutine(routine)}
                  className={`w-full p-6 rounded-[2rem] border transition-all text-left flex flex-col gap-4 group ${
                    selectedRoutine?.id === routine.id 
                      ? 'bg-primary/10 border-primary shadow-lg shadow-primary/10' 
                      : 'bg-surface-low border-white/5 hover:border-white/20'
                  }`}
                >
                  <div className="flex justify-between items-start w-full">
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-bold ${
                        selectedRoutine?.id === routine.id ? 'bg-primary text-background' : 'bg-surface-high text-text-secondary'
                      }`}>
                        {routine.memberName?.charAt(0) || <User size={18} />}
                      </div>
                      <div>
                        <h4 className="text-sm font-bold text-text-main group-hover:text-primary transition-colors leading-none mb-1">{routine.name}</h4>
                        <p className="text-[10px] text-text-secondary uppercase tracking-widest font-bold">Socio: {routine.memberName || 'N/A'}</p>
                      </div>
                    </div>
                    <ChevronRight size={16} className={`transition-transform ${selectedRoutine?.id === routine.id ? 'rotate-90 text-primary' : 'text-text-secondary'}`} />
                  </div>

                  <div className="flex items-center justify-between w-full mt-2">
                    <span className={`px-3 py-1 rounded-full text-[8px] font-black uppercase tracking-widest border ${
                      routine.status === 'ACTIVE' ? 'bg-primary/10 text-primary border-primary/20' : 'bg-surface-high text-text-secondary border-white/5'
                    }`}>
                      {routine.status}
                    </span>
                    <span className="text-[9px] font-bold text-text-secondary flex items-center gap-1 opacity-40 italic">
                      <Calendar size={10} /> {new Date(routine.startDate).toLocaleDateString()}
                    </span>
                  </div>
                </button>
              ))
            )}
          </div>
        </div>

        <div className="xl:col-span-2">
          {selectedRoutine ? (
            <div className="bg-surface-low p-10 rounded-[3rem] border border-white/5 shadow-2xl animate-in fade-in zoom-in duration-500 surface-lift h-full overflow-hidden">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-12">
                <div className="flex items-center gap-6">
                  <div className="w-16 h-16 rounded-3xl bg-primary/20 flex items-center justify-center text-primary shadow-inner">
                    <TrendingUp size={32} />
                  </div>
                  <div>
                    <h2 className="text-3xl font-display font-black text-text-main uppercase italic leading-none mb-2">{selectedRoutine.name}</h2>
                    <p className="text-xs text-text-secondary font-bold uppercase tracking-[0.2em] flex items-center gap-2">
                      <User size={14} className="text-primary" /> Alumno: {selectedRoutine.memberName}
                    </p>
                  </div>
                </div>
                <div className="flex gap-3">
                   <Button 
                    variant="secondary" 
                    className="gap-2 text-[10px] font-black uppercase tracking-widest border-white/10"
                    onClick={() => navigate(`/routines/${selectedRoutine.id}`)}
                   >
                     <Eye size={16} /> Ver Plan
                   </Button>
                </div>
              </div>

              {/* REUTILIZACIÓN DEL COMPONENTE DE PROGRESO */}
              <div className="mt-8 h-full overflow-y-auto pr-4 custom-scrollbar" style={{ maxHeight: 'calc(100vh - 350px)' }}>
                <ProgressDashboard memberId={selectedRoutine.memberId} showTitle={false} />
              </div>
            </div>
          ) : (
            <div className="h-full min-h-[500px] flex flex-col items-center justify-center text-center bg-surface-low/30 border border-dashed border-white/5 rounded-[3rem] p-12">
               <TrendingUp size={64} className="text-surface-med mb-8 opacity-10" />
               <h3 className="text-xl font-display font-black text-text-main uppercase italic opacity-20">Selecciona una rutina para auditar el progreso</h3>
               <p className="text-xs text-text-secondary/30 font-bold uppercase tracking-widest mt-4">Podrás ver la evolución del socio paso a paso</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
