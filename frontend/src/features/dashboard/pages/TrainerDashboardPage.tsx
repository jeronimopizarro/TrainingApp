import React, { useState } from 'react';
import { 
  ClipboardList, 
  User, 
  Calendar, 
  Dumbbell, 
  AlertTriangle, 
  Target,
  ArrowRight,
  CheckCircle2,
  Clock
} from 'lucide-react';
import { useTrainerDashboard } from '../../trainer/hooks/useTrainerDashboard';
import { Button } from '@/shared/components/Button';
import { authService } from '../../auth/services/auth.service';
import { useNavigate } from 'react-router-dom';

export const TrainerDashboardPage = () => {
  const { requests, isLoading, error, takeRequest } = useTrainerDashboard();
  const userData = authService.getUserData();
  const navigate = useNavigate();
  
  const [activeTab, setActiveTab] = useState<'mine' | 'global'>('mine');

  if (isLoading) return <div className="p-10 text-text-secondary animate-pulse font-display font-bold uppercase tracking-widest text-center">Cargando solicitudes...</div>;
  if (error) return <div className="p-10 text-error text-center font-bold">{error}</div>;

  const myRequests = requests.filter(r => r.targetTrainerId === userData?.userId);
  const globalRequests = requests.filter(r => r.targetTrainerId === null);
  
  const displayRequests = activeTab === 'mine' ? myRequests : globalRequests;

  const handleTakeRequest = async (request: any) => {
    const success = await takeRequest(request.id);
    if (success) {
      // Redirigir al creador de rutinas con los datos del socio y la solicitud
      navigate(`/trainer/routines/builder?memberId=${request.memberId}&requestId=${request.id}&memberName=${encodeURIComponent(request.memberName)}`);
    }
  };

  const RequestCard = ({ request, isMyRequest }: { request: any, isMyRequest?: boolean }) => (
    <div className={`bg-surface-low p-6 rounded-[2rem] border ${isMyRequest ? 'border-primary/30 shadow-lg shadow-primary/5' : 'border-surface-med/20'} hover:border-primary/50 transition-all group surface-lift`}>
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
          <div className={`w-10 h-10 rounded-xl ${isMyRequest ? 'bg-primary/20 text-primary' : 'bg-surface-high text-text-secondary'} flex items-center justify-center font-bold`}>
            {request.memberName.charAt(0)}
          </div>
          <div>
            <h3 className="font-display font-bold text-text-main leading-none">{request.memberName}</h3>
            <p className="text-[10px] text-text-secondary uppercase tracking-widest font-bold mt-1">Socio #{request.memberId}</p>
          </div>
        </div>
        <div className="flex flex-col items-end">
           <span className="text-[10px] font-black text-primary uppercase tracking-tighter flex items-center gap-1">
            <Clock size={10} /> {new Date(request.requestDate).toLocaleDateString()}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="space-y-1">
          <p className="text-[9px] text-text-secondary uppercase font-bold tracking-widest flex items-center gap-1">
            <Calendar size={10} /> Disponibilidad
          </p>
          <p className="text-xs font-bold text-text-main">{request.availableDays} días / semana</p>
        </div>
        <div className="space-y-1">
          <p className="text-[9px] text-text-secondary uppercase font-bold tracking-widest flex items-center gap-1">
            <Dumbbell size={10} /> Nivel
          </p>
          <p className="text-xs font-bold text-text-main capitalize">{request.experienceLevel.toLowerCase()}</p>
        </div>
      </div>

      <div className="space-y-3 mb-6">
        <div className="p-3 bg-surface-high/50 rounded-xl border border-surface-med/10">
          <p className="text-[9px] text-primary uppercase font-black tracking-widest mb-1 flex items-center gap-1">
            <Target size={10} /> Objetivo Principal
          </p>
          <p className="text-xs text-text-main leading-relaxed italic">"{request.primaryGoal}"</p>
        </div>
        
        {request.injuries && (
          <div className="p-3 bg-error/5 rounded-xl border border-error/10">
            <p className="text-[9px] text-error uppercase font-black tracking-widest mb-1 flex items-center gap-1">
              <AlertTriangle size={10} /> Lesiones / Observaciones
            </p>
            <p className="text-xs text-text-main leading-relaxed italic">"{request.injuries}"</p>
          </div>
        )}
      </div>

      <Button 
        variant="primary" 
        className="w-full justify-center gap-2 py-3 rounded-xl text-[10px] font-black uppercase tracking-[0.2em]"
        onClick={() => handleTakeRequest(request)}
      >
        Tomar Solicitud <ArrowRight size={14} />
      </Button>
    </div>
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-8 sm:mb-12">
        <div>
          <h2 className="text-[10px] sm:text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-2 sm:mb-3">Panel del Entrenador</h2>
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic">
            Gestión de <span className="text-primary-dark">Rutinas</span>.
          </h1>
        </div>
      </header>

      {/* FILTROS / TABS */}
      <div className="flex flex-col sm:flex-row gap-2 p-1.5 bg-surface-low rounded-[1.5rem] sm:rounded-[2rem] border border-white/5 w-full sm:w-fit mb-8 sm:mb-10">
        <button
          onClick={() => setActiveTab('mine')}
          className={`px-6 sm:px-8 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all flex items-center justify-center sm:justify-start gap-3 ${
            activeTab === 'mine' 
              ? 'bg-primary text-background shadow-lg shadow-primary/20' 
              : 'text-text-secondary hover:text-text-main hover:bg-surface-high'
          }`}
        >
          <User size={14} /> Mis Solicitudes
          <span className={`px-2 py-0.5 rounded-full text-[8px] font-black ${
            activeTab === 'mine' ? 'bg-background/20 text-background' : 'bg-surface-high text-primary'
          }`}>
            {myRequests.length}
          </span>
        </button>
        <button
          onClick={() => setActiveTab('global')}
          className={`px-6 sm:px-8 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all flex items-center justify-center sm:justify-start gap-3 ${
            activeTab === 'global' 
              ? 'bg-primary text-background shadow-lg shadow-primary/20' 
              : 'text-text-secondary hover:text-text-main hover:bg-surface-high'
          }`}
        >
          <ClipboardList size={14} /> Globales
          <span className={`px-2 py-0.5 rounded-full text-[8px] font-black ${
            activeTab === 'global' ? 'bg-background/20 text-background' : 'bg-surface-high text-primary'
          }`}>
            {globalRequests.length}
          </span>
        </button>
      </div>

      {/* LISTA DE SOLICITUDES */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
        {displayRequests.length === 0 ? (
          <div className="col-span-full py-20 px-6 bg-surface-low/50 border border-dashed border-surface-med/30 rounded-[3rem] flex flex-col items-center justify-center text-center">
            <CheckCircle2 size={48} className="text-surface-med mb-4 opacity-10" />
            <p className="text-lg font-display font-black text-text-main/20 uppercase tracking-[0.3em] italic">Sin solicitudes pendientes</p>
            <p className="text-[10px] text-text-secondary/40 font-bold uppercase tracking-widest mt-2">Buen trabajo, estás al día con tus alumnos</p>
          </div>
        ) : (
          displayRequests.map(req => (
            <RequestCard 
              key={req.id} 
              request={req} 
              isMyRequest={activeTab === 'mine'} 
            />
          ))
        )}
      </div>
    </div>
  );
};
