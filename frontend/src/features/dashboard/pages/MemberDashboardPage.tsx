import React, { useState, useEffect } from 'react';
import { 
  Calendar as CalendarIcon, 
  Dumbbell, 
  QrCode, 
  Trophy, 
  ArrowRight,
  Loader2,
  AlertCircle,
  Zap,
  PlusCircle,
  MessageSquare,
  CheckCircle2
} from 'lucide-react';
import { useMemberDashboard } from '@/features/dashboard/hooks/useMemberDashboard';
import { useMemberQr } from '@/features/access/hooks/useMemberQr';
import { routineService } from '@/features/routines/services/routine.service';
import { staffService } from '@/features/staff/services/staff.service';
import { authService } from '@/features/auth/services/auth.service';
import { TrainingCalendar } from '@/features/dashboard/components/TrainingCalendar';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { Input } from '@/shared/components/Input';
import { Link, useNavigate } from 'react-router-dom';
import { ExperienceLevel, RequestRoutineMessage } from '@/features/routines/types/routine.types';
import { Trainer } from '@/features/staff/types/staff.types';

export const MemberDashboardPage = () => {
  const { data, loading, error, refresh } = useMemberDashboard();
  const { qrData, loading: qrLoading, refresh: refreshQr } = useMemberQr();
  const [isQrModalOpen, setIsQrModalOpen] = useState(false);
  
  // Routine Request State
  const [trainers, setTrainers] = useState<Trainer[]>([]);
  const [isRequestModalOpen, setIsRequestModalOpen] = useState(false);
  const [requestLoading, setRequestLoading] = useState(false);
  const [requestSuccess, setRequestSuccess] = useState(false);
  const [requestForm, setRequestForm] = useState<RequestRoutineMessage>({
    targetTrainerId: null,
    availableDays: 3,
    experienceLevel: 'BEGINNER',
    primaryGoal: '',
    injuries: ''
  });

  const navigate = useNavigate();

  useEffect(() => {
    const fetchTrainers = async () => {
      const user = authService.getUserData();
      if (user?.gymId) {
        try {
          const staffData = await staffService.getSummary(user.gymId, 'TRAINER');
          setTrainers(staffData.staffMembers.filter(m => m.role === 'TRAINER') as Trainer[]);
        } catch (err) {
          console.error("Error fetching trainers", err);
        }
      }
    };
    fetchTrainers();
  }, []);

  if (loading) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      <Loader2 className="w-12 h-12 text-primary animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs text-text-secondary">Sincronizando Dashboard...</p>
    </div>
  );

  if (error) return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-6 p-10 text-center">
      <AlertCircle className="w-16 h-16 text-error opacity-50" />
      <h2 className="text-2xl font-display font-black text-text-main">¡Oops! Algo salió mal</h2>
      <p className="text-text-secondary max-w-md">{error}</p>
    </div>
  );

  const qrUrl = qrData 
    ? `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(qrData.qrToken)}&bgcolor=121417&color=ffb600`
    : null;

  const handleOpenQr = () => {
    refreshQr();
    setIsQrModalOpen(true);
  };

  const handleRequestRoutine = async (e: React.FormEvent) => {
    e.preventDefault();
    setRequestLoading(true);
    try {
      await routineService.request(requestForm);
      setRequestSuccess(true);
      setTimeout(() => {
        setIsRequestModalOpen(false);
        setRequestSuccess(false);
        refresh();
      }, 2000);
    } catch (err) {
      alert("Error al enviar la solicitud");
    } finally {
      setRequestLoading(false);
    }
  };

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-2 max-h-screen overflow-hidden">
      {/* HEADER - ULTRA COMPACT */}
      <header className="mb-4 animate-in fade-in slide-in-from-left-6 duration-700 delay-150">
        <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Operaciones</h2>
        <h1 className="text-5xl font-display font-black text-text-main tracking-tighter italic leading-none">
          Focus <span className="text-primary-dark">Daily</span>.
        </h1>
      </header>

      {/* BENTO GRID - ROW 1: Identity & Access (4/4/4) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-3 mb-3">
        
        {/* 1. MEMBERSHIP */}
        <div className="lg:col-span-4 animate-in fade-in zoom-in-95 duration-700 delay-200">
          <StatCard 
            label="Estado de Membresía" 
            value={
              <div className="flex items-baseline gap-2">
                <span>{data?.daysUntilExpiration || 0} Días</span>
                <span className="text-[10px] font-black uppercase tracking-[0.2em] opacity-40 italic">restantes</span>
              </div>
            } 
            icon={CalendarIcon} 
            colorClass="text-primary" 
            description="Tiempo restante de acceso" 
            size="normal"
          />
        </div>

        {/* 2. MY PLAN */}
        <div className="lg:col-span-4 animate-in fade-in zoom-in-95 duration-700 delay-250">
          <Link to="/member/routine" className="block h-full">
            <div className="bg-surface-low border border-white/[0.02] p-4 rounded-[1.5rem] h-full flex flex-col justify-between group hover:border-primary/30 transition-all duration-700 shadow-md relative overflow-hidden surface-lift min-h-[120px]">
               <div className="absolute -bottom-10 -left-10 w-28 h-28 bg-primary/5 rounded-full blur-[40px] group-hover:bg-primary/10 transition-all duration-1000" />
               <div className="relative z-10">
                 <div className="w-8 h-8 bg-primary/10 border border-primary/20 rounded-lg flex items-center justify-center text-primary mb-2 group-hover:scale-110 group-hover:-rotate-3 transition-all duration-700">
                   <Dumbbell size={18} />
                 </div>
                 <h3 className="text-xl font-display font-black text-text-main uppercase italic mb-0.5 tracking-tighter">Mi <span className="text-primary">Rutina</span></h3>
                 <p className="text-[10px] text-text-secondary font-bold uppercase tracking-[0.2em] opacity-40">Plan y pesos</p>
               </div>
               <div className="flex justify-end relative z-10">
                  <div className="w-7 h-7 rounded-full bg-surface-high border border-white/5 flex items-center justify-center text-text-secondary group-hover:text-primary transition-all duration-500">
                    <ArrowRight size={14} />
                  </div>
               </div>
            </div>
          </Link>
        </div>

        {/* 3. QR ACCESS CARD */}
        <div className="lg:col-span-4 animate-in fade-in zoom-in-95 duration-700 delay-300">
          <button 
            onClick={handleOpenQr}
            className="w-full h-full bg-surface-low border border-white/[0.02] p-4 rounded-[1.5rem] flex flex-col justify-between group hover:border-primary/40 transition-all duration-700 shadow-md relative overflow-hidden surface-lift min-h-[120px] text-left"
          >
             <div className="absolute -bottom-10 -right-10 w-28 h-28 bg-primary/5 rounded-full blur-[40px] group-hover:bg-primary/10 transition-all duration-1000" />
             <div className="relative z-10">
               <div className="w-8 h-8 bg-primary/10 border border-primary/20 rounded-lg flex items-center justify-center text-primary mb-2 group-hover:scale-110 group-hover:rotate-6 transition-all duration-700">
                 <QrCode size={18} />
               </div>
               <h3 className="text-xl font-display font-black text-text-main uppercase italic mb-0.5 tracking-tighter">Entrada <span className="text-primary">QR</span></h3>
               <p className="text-[10px] text-text-secondary font-bold uppercase tracking-[0.2em] opacity-40">Acceso rápido</p>
             </div>
             <div className="flex justify-end relative z-10">
                <div className="w-7 h-7 rounded-full bg-surface-high border border-white/5 flex items-center justify-center text-text-secondary group-hover:text-primary transition-all duration-500">
                  <ArrowRight size={14} />
                </div>
             </div>
          </button>
        </div>
      </div>

      {/* BENTO GRID - ROW 2: Training Focus (8/4) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 items-stretch overflow-hidden">
        
        {/* 4. CALENDAR (LEFT) */}
        <div className="lg:col-span-8 animate-in fade-in slide-in-from-left-8 duration-1000 delay-400">
          <TrainingCalendar trainingDays={data?.trainingDaysThisMonth || []} />
        </div>

        {/* 5. NEXT SESSION (RIGHT) */}
        <div className="lg:col-span-4 animate-in fade-in zoom-in-95 duration-700 delay-350">
          <div className="bg-surface-low rounded-[2rem] border border-white/[0.02] p-6 flex flex-col items-center justify-center shadow-lg relative group overflow-hidden surface-lift h-full min-h-[220px] text-center">
            <div className="absolute -bottom-10 -right-10 w-32 h-32 bg-white/[0.01] rounded-full blur-[40px] group-hover:bg-white/[0.03] transition-all duration-1000" />
            
            <div className="relative z-10 w-full flex flex-col items-center">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface-high border border-white/5 mb-4">
                <Zap size={10} className="fill-text-secondary text-text-secondary opacity-50" />
                <span className="text-[10px] font-black uppercase tracking-[0.4em] text-text-secondary">Próximo Entrenamiento</span>
              </div>
              
              {data?.activeRoutine ? (
                <>
                  <h4 className="text-2xl font-display font-black text-text-main italic mb-6 uppercase tracking-tighter leading-tight px-2">
                    {data.activeRoutine.suggestedDay?.name || 'Continuar Entrenamiento'}
                  </h4>
                  <Button 
                    onClick={() => {
                      if (data?.activeRoutine?.suggestedDay) {
                        navigate(`/member/workout/${data.activeRoutine.routineId}/day/${data.activeRoutine.suggestedDay.dayId}`);
                      } else if (data?.activeRoutine) {
                        navigate(`/member/routine`);
                      }
                    }}
                    variant="primary" 
                    className="w-full py-4 rounded-xl text-xs font-black uppercase tracking-[0.2em] shadow-lg hover:scale-105 active:scale-95 transition-all group"
                    disabled={!data?.activeRoutine}
                  >
                    <span className="flex items-center justify-center gap-3">
                      <Dumbbell size={16} />
                      ¡Entrenar Ahora!
                    </span>
                  </Button>
                </>
              ) : data?.hasPendingRequest ? (
                <div className="flex flex-col items-center gap-4 py-4 animate-in fade-in duration-700">
                  <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center text-primary mb-2 border border-primary/20">
                    <Loader2 size={24} className="animate-spin" />
                  </div>
                  <div>
                    <h4 className="text-sm font-black text-text-main uppercase tracking-widest italic leading-none mb-2">Solicitud en Proceso</h4>
                    <p className="text-[10px] text-text-secondary font-medium max-w-[200px] mx-auto">Tu entrenador está diseñando tu nuevo plan. Te notificaremos pronto.</p>
                  </div>
                </div>
              ) : (
                <div className="flex flex-col gap-3 w-full">
                  <h4 className="text-sm font-bold text-text-secondary uppercase mb-2">No tienes una rutina activa</h4>
                  <Button 
                    onClick={() => navigate('/member/routine/builder')}
                    variant="primary" 
                    className="w-full py-3 rounded-xl text-[10px] font-black uppercase tracking-widest flex items-center justify-center gap-2"
                  >
                    <PlusCircle size={14} />
                    Crear Rutina Propia
                  </Button>
                  <Button 
                    onClick={() => setIsRequestModalOpen(true)}
                    variant="ghost" 
                    className="w-full py-3 rounded-xl text-[10px] font-black uppercase tracking-widest border border-white/5 hover:border-primary/30 flex items-center justify-center gap-2"
                  >
                    <MessageSquare size={14} />
                    Solicitar al Staff
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* QR MODAL */}
      <Modal 
        isOpen={isQrModalOpen} 
        onClose={() => setIsQrModalOpen(false)} 
        title="Acceso Gimnasio"
      >
        <div className="flex flex-col items-center justify-center py-10 text-center">
           <div className="relative mb-10 group">
             <div className="absolute -inset-4 bg-primary/20 rounded-[3rem] blur-2xl opacity-50 group-hover:opacity-100 transition-opacity" />
             <div className="relative bg-surface-high p-6 rounded-[2.5rem] border border-white/5 shadow-2xl">
                {qrLoading ? (
                  <div className="w-64 h-64 flex flex-col items-center justify-center gap-4">
                    <Loader2 className="w-12 h-12 text-primary animate-spin" />
                    <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary">Generando Código...</p>
                  </div>
                ) : qrUrl ? (
                  <img src={qrUrl} alt="QR Access" className="w-64 h-64 rounded-2xl" />
                ) : (
                  <div className="w-64 h-64 flex items-center justify-center text-error">
                    <AlertCircle size={48} />
                  </div>
                )}
             </div>
           </div>

           <div className="max-w-xs">
             <h4 className="text-xl font-display font-black text-text-main uppercase italic mb-3">Escanea en la Entrada</h4>
             <p className="text-xs text-text-secondary font-medium leading-relaxed mb-8">
               Este código es personal e intransferible. Expira automáticamente en <span className="text-primary font-bold">60 segundos</span> por seguridad.
             </p>
             <Button 
                onClick={refreshQr} 
                variant="ghost" 
                className="text-[10px] font-black uppercase tracking-widest text-text-secondary hover:text-primary"
              >
               Actualizar Código
             </Button>
           </div>
        </div>
      </Modal>

      {/* REQUEST ROUTINE MODAL */}
      <Modal
        isOpen={isRequestModalOpen}
        onClose={() => setIsRequestModalOpen(false)}
        title="Solicitar Nueva Rutina"
      >
        {requestSuccess ? (
          <div className="flex flex-col items-center justify-center py-12 text-center animate-in zoom-in duration-500">
            <div className="w-20 h-20 bg-primary/20 rounded-full flex items-center justify-center mb-6">
              <CheckCircle2 size={40} className="text-primary" />
            </div>
            <h4 className="text-2xl font-display font-black text-text-main uppercase italic mb-2">¡Solicitud Enviada!</h4>
            <p className="text-sm text-text-secondary max-w-xs">Tu entrenador revisará tu perfil y te asignará un nuevo plan pronto.</p>
          </div>
        ) : (
          <form onSubmit={handleRequestRoutine} className="flex flex-col gap-6 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Días Disponibles</label>
                <select 
                  value={requestForm.availableDays}
                  onChange={(e) => setRequestForm({...requestForm, availableDays: Number(e.target.value)})}
                  className="bg-surface-high border border-white/5 rounded-xl p-3 text-sm font-bold text-text-main focus:outline-none focus:border-primary/50"
                >
                  {[1,2,3,4,5,6,7].map(d => <option key={d} value={d}>{d} Días</option>)}
                </select>
              </div>
              <div className="flex flex-col gap-2">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Nivel</label>
                <select 
                  value={requestForm.experienceLevel}
                  onChange={(e) => setRequestForm({...requestForm, experienceLevel: e.target.value as ExperienceLevel})}
                  className="bg-surface-high border border-white/5 rounded-xl p-3 text-sm font-bold text-text-main focus:outline-none focus:border-primary/50"
                >
                  <option value="BEGINNER">Principiante</option>
                  <option value="INTERMEDIATE">Intermedio</option>
                  <option value="ADVANCED">Avanzado</option>
                </select>
              </div>
            </div>

            <div className="flex flex-col gap-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Entrenador Preferido (Opcional)</label>
              <select 
                value={requestForm.targetTrainerId || ''}
                onChange={(e) => setRequestForm({...requestForm, targetTrainerId: e.target.value ? Number(e.target.value) : null})}
                className="bg-surface-high border border-white/5 rounded-xl p-3 text-sm font-bold text-text-main focus:outline-none focus:border-primary/50"
              >
                <option value="">Cualquiera (Staff)</option>
                {trainers.map(t => (
                  <option key={t.id} value={t.id}>{t.firstName} {t.lastName}</option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Objetivo Principal</label>
              <Input 
                placeholder="Ej: Ganar masa muscular, Perder grasa..." 
                value={requestForm.primaryGoal}
                onChange={(e) => setRequestForm({...requestForm, primaryGoal: e.target.value})}
                required
              />
            </div>

            <div className="flex flex-col gap-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Lesiones o Notas (Opcional)</label>
              <textarea 
                className="bg-surface-high border border-white/5 rounded-xl p-4 text-sm font-medium text-text-main focus:outline-none focus:border-primary/50 min-h-[100px] resize-none"
                placeholder="Indica si tienes alguna limitación física..."
                value={requestForm.injuries}
                onChange={(e) => setRequestForm({...requestForm, injuries: e.target.value})}
              />
            </div>

            <Button 
              type="submit" 
              variant="primary" 
              className="w-full py-4 mt-2 rounded-xl text-xs font-black uppercase tracking-widest"
              disabled={requestLoading}
            >
              {requestLoading ? <Loader2 className="animate-spin" /> : "Enviar Solicitud"}
            </Button>
          </form>
        )}
      </Modal>
    </div>
  );
};
