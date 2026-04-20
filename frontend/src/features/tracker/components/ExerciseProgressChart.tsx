import React from 'react';
import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  AreaChart,
  Area
} from 'recharts';
import { ProgressDataPoint } from '../types/tracker.types';

interface ExerciseProgressChartProps {
  data: ProgressDataPoint[];
  exerciseName: string;
}

export const ExerciseProgressChart: React.FC<ExerciseProgressChartProps> = ({ data, exerciseName }) => {
  if (!data || data.length === 0) {
    return (
      <div className="h-64 flex items-center justify-center bg-surface-high/20 rounded-[2rem] border border-white/5">
        <p className="text-text-secondary text-xs font-black uppercase tracking-widest">
          No hay datos suficientes para graficar
        </p>
      </div>
    );
  }

  // Format date for XAxis
  const formattedData = data.map(point => ({
    ...point,
    displayDate: new Date(point.date).toLocaleDateString('es-ES', { day: '2-digit', month: 'short' })
  }));

  return (
    <div className="w-full h-80 mt-6 animate-in fade-in duration-700">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={formattedData}>
          <defs>
            <linearGradient id="colorE1rm" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#89acff" stopOpacity={0.3}/>
              <stop offset="95%" stopColor="#89acff" stopOpacity={0}/>
            </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#ffffff10" vertical={false} />
            <XAxis 
            dataKey="displayDate" 
            stroke="#ffffff40" 
            fontSize={10} 
            fontWeight="bold"
            axisLine={false}
            tickLine={false}
            dy={10}
            />
            <YAxis 
            stroke="#ffffff40" 
            fontSize={10} 
            fontWeight="bold"
            axisLine={false}
            tickLine={false}
            domain={['auto', 'auto']}
            />
            <Tooltip 
            contentStyle={{ 
              backgroundColor: '#111111', 
              border: '1px solid #ffffff10', 
              borderRadius: '16px',
              boxShadow: '0 10px 30px rgba(0,0,0,0.5)'
            }}
            itemStyle={{ color: '#89acff', fontWeight: 'bold' }}
            labelStyle={{ color: '#ffffff', marginBottom: '4px', fontWeight: 'black' }}
            />
            <Area 
            type="monotone" 
            dataKey="e1rm" 
            stroke="#89acff" 
            strokeWidth={4}
            fillOpacity={1} 
            fill="url(#colorE1rm)" 
            animationDuration={2000}
            />

        </AreaChart>
      </ResponsiveContainer>
      <div className="flex justify-between items-center px-4 mt-2">
         <p className="text-[10px] font-black text-text-secondary uppercase tracking-[0.2em]">Evolución de Carga Estimada (e1RM)</p>
         <p className="text-[10px] font-black text-primary uppercase tracking-[0.2em]">{exerciseName}</p>
      </div>
    </div>
  );
};
