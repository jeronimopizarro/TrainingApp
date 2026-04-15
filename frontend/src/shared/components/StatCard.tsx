import React from 'react';
import { LucideIcon, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { clsx } from 'clsx';

interface StatCardProps {
  label: string;
  value: React.ReactNode;
  icon: LucideIcon;
  trend?: 'up' | 'down';
  trendValue?: string;
  colorClass?: string; // Para el color del icono de fondo (e.g., 'text-primary')
  description?: string; // Texto sutil debajo de la tendencia
  size?: 'normal' | 'compact' | 'large';
}

/**
 * StatCard Kinetic: Componente centralizado para métricas en TrainingApp.
 * Implementa profundidad tonal, iconos de fondo con opacidad baja y efectos de elevación.
 */
export const StatCard: React.FC<StatCardProps> = ({
  label,
  value,
  icon: Icon,
  trend,
  trendValue,
  colorClass = "text-text-main",
  description = "vs mes pasado",
  size = 'normal'
}) => {
  const isCompact = size === 'compact';
  const isLarge = size === 'large';

  return (
    <div className={clsx(
      "bg-surface-low relative overflow-hidden group hover:bg-surface-low/80 transition-all cursor-default surface-lift border border-white/[0.02] flex flex-col justify-center",
      isCompact ? "p-5 rounded-[1.5rem]" : isLarge ? "p-12 rounded-[3rem] min-h-[180px]" : "p-8 rounded-[2rem]"
    )}>
      {/* Icono de Fondo Decorativo */}
      <div className={clsx(
        "absolute opacity-[0.03] group-hover:opacity-[0.06] group-hover:scale-110 transition-all duration-500",
        colorClass,
        isCompact ? "-right-2 -bottom-2" : isLarge ? "-right-8 -bottom-8" : "-right-4 -bottom-4"
      )}>
        <Icon size={isCompact ? 80 : isLarge ? 200 : 140} strokeWidth={1} />
      </div>

      <div className="relative z-10">
        {/* Etiqueta Superior */}
        <p className={clsx(
          "uppercase tracking-[0.2em] text-text-secondary font-bold opacity-60",
          isCompact ? "text-[8px] mb-2" : isLarge ? "text-[12px] mb-6" : "text-[10px] mb-4"
        )}>
          {label}
        </p>

        {/* Valor Principal (Lexend / Display) */}
        <h3 className={clsx(
          "font-display font-black text-text-main tracking-tighter",
          isCompact ? "text-2xl mb-2" : isLarge ? "text-6xl mb-6" : "text-4xl mb-4"
        )}>
          {value}
        </h3>

        {/* Sección de Tendencia (Si existe) */}
        {(trend || trendValue) && (
          <div className="flex items-center gap-2">
            <div className={clsx(
              "flex items-center gap-1 rounded-lg font-bold",
              trend === 'up' ? 'bg-green-500/10 text-green-400' : 'bg-error/10 text-error',
              isCompact ? "px-1.5 py-0.5 text-[8px]" : isLarge ? "px-3 py-1.5 text-[12px]" : "px-2 py-1 text-[10px]"
            )}>
              {trend === 'up' ? <ArrowUpRight size={isCompact ? 10 : isLarge ? 14 : 12} /> : <ArrowDownRight size={isCompact ? 10 : isLarge ? 14 : 12} />}
              {trendValue}
            </div>
            <span className={clsx(
              "text-text-secondary font-medium opacity-40 uppercase tracking-widest italic leading-none",
              isCompact ? "text-[7px]" : isLarge ? "text-[11px]" : "text-[9px]"
            )}>
              {description}
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
