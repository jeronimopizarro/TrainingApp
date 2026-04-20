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

const StatusBadge = ({ granted, compact = false }: { granted: boolean; compact?: boolean }) => {
  if (compact) {
    return (
      <div className={`flex items-center justify-center w-7 h-7 rounded-lg border transition-all duration-500 ${
        granted 
        ? 'bg-green-500/10 text-green-400 border-green-500/20' 
        : 'bg-error/10 text-error border-error/20'
      }`}>
        {granted ? <UserCheck size={14} /> : <UserX size={14} />}
      </div>
    );
  }

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
  const formattedTime = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  const initials = `${log.memberFirstName[0]}${log.memberLastName[0]}`.toUpperCase();

  return (
    <div className="group flex flex-col sm:flex-row sm:items-center gap-3 sm:gap-6 p-4 sm:p-6 bg-surface-low hover:bg-surface-med/50 transition-all cursor-pointer border-b border-white/[0.02] last:border-0 first:rounded-t-[1.5rem] last:rounded-b-[1.5rem]">
      <div className="flex items-center gap-4 flex-1 min-w-0">
        <div className={`w-10 h-10 sm:w-12 sm:h-12 rounded-xl sm:rounded-2xl bg-surface-high flex items-center justify-center font-display font-black group-hover:scale-105 transition-all duration-300 shadow-xl border border-white/5 shrink-0 ${log.accessGranted ? 'text-primary' : 'text-error'}`}>
          {initials}
        </div>
        
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-3 mb-1 sm:mb-1.5">
            <h3 className="text-sm sm:text-base font-bold text-text-main group-hover:text-primary transition-colors leading-none truncate">
              {log.memberFirstName} {log.memberLastName}
            </h3>
            {/* Icono compacto: Solo visible en móvil */}
            <div className="sm:hidden">
              <StatusBadge granted={log.accessGranted} compact />
            </div>
          </div>
          {/* Fecha y hora */}
          <p className="flex text-[9px] sm:text-xs text-text-secondary font-bold uppercase tracking-widest items-center gap-1.5">
            <Clock size={10} /> {formattedDate} • {formattedTime}
          </p>
        </div>
      </div>

      <div className="hidden lg:block flex-1 max-w-xs">
        <p className="text-xs font-medium text-text-secondary italic truncate border-l border-white/10 pl-3">
          {log.message}
        </p>
      </div>

      <div className="flex items-center justify-between sm:justify-end gap-4 w-full sm:w-auto shrink-0">
        <div className="hidden sm:block">
          <StatusBadge granted={log.accessGranted} />
        </div>
        <div className="p-2 text-text-secondary opacity-0 sm:group-hover:opacity-100 group-hover:text-primary transition-all">
          <ArrowUpRight size={18} />
        </div>
      </div>
    </div>
  );
};

export const AccessLogsPage = () => {
  const { logs, stats, loading, error, refreshLogs, currentFilter } = useAccessLogs();
  const [isInitialLoad, setIsInitialLoad] = useState(true);

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
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-8 sm:mb-12">
        <div>
          <h2 className="text-[10px] sm:text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-2 sm:mb-3">Seguridad</h2>
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic">
            Auditoría de <span className="text-primary-dark">Accesos</span>.
          </h1>
        </div>
        <div className="flex items-center gap-3 bg-surface-low p-2 rounded-2xl border border-white/5 w-fit">
          <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
            <ShieldCheck size={20} />
          </div>
          <div className="pr-4">
            <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary">Estado del Sistema</p>
            <p className="text-xs font-bold text-green-400 flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
              Monitoreo Activo
            </p>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6 mb-8 sm:mb-12">
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
        <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-full sm:w-fit border border-white/[0.03] overflow-x-auto custom-scrollbar">
          <Button 
            onClick={() => handleFilterChange(undefined)} 
            variant={currentFilter === undefined ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${currentFilter === undefined ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Todos
          </Button>
          <Button 
            onClick={() => handleFilterChange(true)} 
            variant={currentFilter === true ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${currentFilter === true ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Permitidos
          </Button>
          <Button 
            onClick={() => handleFilterChange(false)} 
            variant={currentFilter === false ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${currentFilter === false ? '' : 'text-text-secondary hover:text-text-main'}`}
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
