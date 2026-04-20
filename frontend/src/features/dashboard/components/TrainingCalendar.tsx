import React from 'react';
import { clsx } from 'clsx';

interface TrainingCalendarProps {
  trainingDays: string[]; // List of ISO dates
}

export const TrainingCalendar: React.FC<TrainingCalendarProps> = ({ trainingDays }) => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  
  const firstDayOfMonth = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  
  const monthName = new Intl.DateTimeFormat('es-ES', { month: 'long' }).format(now);
  
  // Normalize training days to just the date string (YYYY-MM-DD)
  const normalizedTrainingDays = trainingDays.map(d => d.split('T')[0]);

  const days = Array.from({ length: daysInMonth }, (_, i) => {
    const day = i + 1;
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    const isTrainingDay = normalizedTrainingDays.includes(dateStr);
    const isToday = day === now.getDate();
    
    return { day, isTrainingDay, isToday };
  });

  const weekDays = ['D', 'L', 'M', 'M', 'J', 'V', 'S'];

  return (
    <div className="bg-surface-low p-6 rounded-[2rem] border border-white/[0.03] shadow-lg h-full flex flex-col justify-between surface-lift group transition-all duration-700 hover:border-primary/20 w-full overflow-hidden">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-[10px] font-black uppercase tracking-[0.4em] text-text-secondary mb-0.5">
            Progreso <span className="text-primary">{monthName}</span>
          </h3>
          <p className="text-xl font-display font-black text-text-main italic uppercase tracking-tighter leading-none">
            Actividad <span className="text-primary">Mensual</span>
          </p>
        </div>

        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-primary shadow-[0_0_10px_rgba(137,172,255,0.6)]" />
          <span className="text-[10px] font-black text-text-secondary uppercase tracking-widest">Entrenado</span>
        </div>
      </div>

      <div className="flex-1 flex flex-col justify-center">
        <div className="grid grid-cols-7 gap-2 mb-2">
          {weekDays.map((wd, idx) => (
            <div key={`${wd}-${idx}`} className="text-center text-[9px] font-black text-text-secondary uppercase py-1">
              {wd}
            </div>
          ))}
        </div>
        
        <div className="grid grid-cols-7 gap-2">
          {Array.from({ length: firstDayOfMonth }).map((_, i) => (
            <div key={`empty-${i}`} className="h-8" />
          ))}
          {days.map(({ day, isTrainingDay, isToday }, idx) => (
            <div 
              key={day}
              className={clsx(
                "h-9 rounded-xl flex items-center justify-center text-[11px] font-black transition-all duration-500 border",
                isTrainingDay 
                  ? "bg-primary/20 text-primary border-primary/40 shadow-[0_0_12px_rgba(137,172,255,0.1)]" 
                  : "bg-surface-high/20 text-text-secondary border-white/[0.03] hover:border-white/10 hover:bg-surface-high/40",
                isToday && !isTrainingDay && "border-primary/60 text-white ring-2 ring-primary/10"
              )}
            >
              {day}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
