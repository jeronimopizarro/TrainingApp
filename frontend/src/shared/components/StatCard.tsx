import React from 'react';
import { LucideIcon, ArrowUpRight, ArrowDownRight } from 'lucide-react';

interface StatCardProps {
  label: string;
  value: string | number;
  icon: LucideIcon;
  trend?: 'up' | 'down';
  trendValue?: string;
  colorClass?: string; // Para el color del icono de fondo (e.g., 'text-primary')
  description?: string; // Texto sutil debajo de la tendencia
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
  description = "vs mes pasado"
}) => {
  return (
    <div className="bg-surface-high p-8 rounded-[2rem] relative overflow-hidden group hover:bg-surface-high/80 transition-all cursor-default surface-lift border border-white/[0.02]">
      {/* Icono de Fondo Decorativo */}
      <div className={`absolute -right-4 -bottom-4 opacity-[0.03] group-hover:opacity-[0.06] group-hover:scale-110 transition-all duration-500 ${colorClass}`}>
        <Icon size={140} strokeWidth={1} />
      </div>

      <div className="relative z-10">
        {/* Etiqueta Superior */}
        <p className="text-[10px] uppercase tracking-[0.2em] text-text-secondary font-bold mb-4 opacity-60">
          {label}
        </p>

        {/* Valor Principal (Lexend / Display) */}
        <h3 className="text-4xl font-display font-black text-text-main tracking-tighter mb-4">
          {value}
        </h3>

        {/* Sección de Tendencia (Si existe) */}
        {(trend || trendValue) && (
          <div className="flex items-center gap-2">
            <div className={`flex items-center gap-1 px-2 py-1 rounded-lg text-[10px] font-bold ${
              trend === 'up' ? 'bg-green-500/10 text-green-400' : 'bg-error/10 text-error'
            }`}>
              {trend === 'up' ? <ArrowUpRight size={12} /> : <ArrowDownRight size={12} />}
              {trendValue}
            </div>
            <span className="text-[9px] text-text-secondary font-medium opacity-40 uppercase tracking-widest italic leading-none">
              {description}
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
