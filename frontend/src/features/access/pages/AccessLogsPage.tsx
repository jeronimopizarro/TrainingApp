import React, { useState } from 'react';
import { 
  ShieldCheck, 
  ShieldAlert, 
  Clock, 
  UserCheck, 
  UserX,
  History,
  Activity,
  ArrowUpRight
} from 'lucide-react';
import { useAccessLogs } from '../hooks/useAccessLogs';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';

const StatusBadge = ({ granted }: { granted: boolean }) => {
  return (
    <div className={`flex items-center gap-2 px-4 py-1.5 rounded-full text-xs font-black uppercase tracking-widest border transition-all duration-500 ${
      granted 
      ? 'bg-green-500/10 text-green-400 border-green-500/20 shadow-[0_0_15px_rgba(34,197,94,0.1)]' 
      : 'bg-error/10 text-error border-error/20 shadow-[0_0_15px_rgba(239,68,68,0.1)]'
    }`}>
      {granted ? <UserCheck size={14} /> : <UserX size={14} />}
      {granted ? 'Permitido' : 'Denegado'}
    </div>
  );
};

const AccessLogRow = ({ log }: { log: any }) => {
  const date = new Date(log.timestamp);
  const formattedDate = date.toLocaleDateString();
  const formattedTime = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const initials = `${log.memberFirstName[0]}${log.memberLastName[0]}`.toUpperCase();

  return (
    <div className="group flex items-center gap-6 p-6 bg-surface-low hover:bg-surface-med/50 transition-all cursor-pointer border-b border-white/[0.02] last:border-0 first:rounded-t-[1.5rem] last:rounded-b-[1.5rem]">
      <div className={`w-12 h-12 rounded-2xl bg-surface-high flex items-center justify-center font-display font-black group-hover:scale-105 transition-all duration-300 shadow-xl border border-white/5 ${log.accessGranted ? 'text-primary' : 'text-error'}`}>
        {initials}
      </div>
      
      <div className="flex-1 min-w-0">
        <h3 className="text-base font-bold text-text-main group-hover:text-primary transition-colors leading-none mb-1.5">
          {log.memberFirstName} {log.memberLastName}
        </h3>
        <p className="text-xs text-text-secondary opacity-60 font-bold uppercase tracking-widest flex items-center gap-2">
          <Clock size={12} /> {formattedDate} • {formattedTime}
        </p>
      </div>

      <div className="hidden lg:block w-64">
        <p className="text-xs font-medium text-text-secondary italic truncate">
          "{log.message}"
        </p>
      </div>

      <div className="w-44 flex justify-center">
        <StatusBadge granted={log.accessGranted} />
      </div>

      <div className="w-12 flex justify-end">
        <div className="p-2 text-text-secondary opacity-0 group-hover:opacity-100 group-hover:text-primary transition-all">
          <ArrowUpRight size={20} />
        </div>
      </div>
    </div>
  );
};

export const AccessLogsPage = () => {
  const { logs, stats, loading, error, refreshLogs, currentFilter } = useAccessLogs();
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  // Control de carga inicial para la animación
  React.useEffect(() => {
    if (!loading && isInitialLoad) {
      setIsInitialLoad(false);
    }
  }, [loading, isInitialLoad]);

  const handleFilterChange = (granted?: boolean) => {
    refreshLogs(granted);
  };

  if (loading && isInitialLoad) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-xs">Sincronizando Auditoría...</p>
    </div>
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Seguridad</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Auditoría de <span className="text-primary-dark">Accesos</span>.
          </h1>
        </div>
        <div className="flex items-center gap-3 bg-surface-low p-2 rounded-2xl border border-white/5">
          <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
            <ShieldCheck size={20} />
          </div>
          <div className="pr-4">
            <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary opacity-50">Estado del Sistema</p>
            <p className="text-xs font-bold text-green-400 flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
              Monitoreo Activo
            </p>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <StatCard 
          label="Ingresos Hoy" 
          value={stats.successfulToday} 
          icon={UserCheck} 
          colorClass="text-primary" 
          description="Accesos autorizados" 
        />
        <StatCard 
          label="Intentos Fallidos" 
          value={stats.failedToday} 
          icon={ShieldAlert} 
          colorClass="text-error" 
          description="Accesos denegados" 
        />
        <StatCard 
          label="Movimientos Hoy" 
          value={stats.successfulToday + stats.failedToday} 
          icon={Activity} 
          colorClass="text-blue-400" 
          description="Total de actividad diaria" 
        />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-fit border border-white/[0.03]">
          <Button 
            onClick={() => handleFilterChange(undefined)} 
            variant={currentFilter === undefined ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${currentFilter === undefined ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Todos
          </Button>
          <Button 
            onClick={() => handleFilterChange(true)} 
            variant={currentFilter === true ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${currentFilter === true ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Permitidos
          </Button>
          <Button 
            onClick={() => handleFilterChange(false)} 
            variant={currentFilter === false ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${currentFilter === false ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Denegados
          </Button>
        </div>
      </div>

      <div className={`bg-surface-low/30 rounded-[1.5rem] border border-white/[0.03] shadow-2xl overflow-hidden transition-all duration-500 ${loading ? 'opacity-40 grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
        {logs.length === 0 ? (
          <div className="p-20 text-center">
            <History size={48} className="mx-auto text-surface-high mb-4" />
            <p className="text-text-secondary font-bold italic">No se registran movimientos en este filtro</p>
          </div>
        ) : (
          <div className="flex flex-col">
            {logs.map(log => (
              <AccessLogRow key={log.id} log={log} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
