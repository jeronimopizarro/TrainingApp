import React from 'react';
import { 
  DollarSign, 
  Users, 
  TrendingUp, 
  AlertCircle, 
  Package, 
  ArrowUpRight, 
  Clock
} from 'lucide-react';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer, 
  Cell
} from 'recharts';
import { useDashboard } from '../hooks/useDashboard';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';

const CustomTooltip = ({ active, payload }: any) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-surface-high p-4 rounded-2xl shadow-2xl border border-surface-med/20 backdrop-blur-xl">
        <p className="text-[10px] uppercase tracking-widest text-text-secondary font-bold mb-2">{payload[0].payload.name}</p>
        <p className="text-xl font-display font-black text-primary">${payload[0].value.toLocaleString()}</p>
      </div>
    );
  }
  return null;
};

export const AdminDashboardPage = () => {
  const { data, isLoading, error } = useDashboard();

  if (isLoading) return <div className="p-10 text-text-secondary animate-pulse font-display font-bold uppercase tracking-widest text-center">Iniciando TrainingApp...</div>;
  if (error) return <div className="p-10 text-error text-center font-bold">{error}</div>;
  if (!data) return null;

  const chartData = [
    { name: 'Membresías', value: data.financialSummary.membershipRevenue || 0, color: '#89acff' },
    { name: 'Productos', value: data.financialSummary.productsRevenue || 0, color: '#8496ff' }
  ];

  const expiringCount = data.expiringMemberships.length;

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-8 sm:mb-12">
        <div>
          <h2 className="text-[10px] sm:text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-2 sm:mb-3">Centro de Mando</h2>
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-display font-black text-text-main tracking-tight italic">
            Control de <span className="text-primary-dark">Rendimiento</span>.
          </h1>
        </div>
      </header>

      {/* KPI GRID - DATOS CALCULADOS EN BACKEND */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-8 sm:mb-12">
        <StatCard 
          label="Ingresos Mensuales" 
          value={`$${data.financialSummary.monthlyRevenue.toLocaleString()}`} 
          trend={data.financialSummary.monthlyRevenue >= data.financialSummary.lastMonthRevenue ? 'up' : 'down'} 
          trendValue={data.financialSummary.revenueGrowthPercentage} 
          icon={DollarSign} 
        />
        <StatCard 
          label="Socios Activos" 
          value={data.audienceSummary.activeMembers} 
          trend={data.audienceSummary.activeMembersGrowth >= 0 ? 'up' : 'down'} 
          trendValue={`${data.audienceSummary.activeMembersGrowth >= 0 ? '+' : ''}${data.audienceSummary.activeMembersGrowth} socios`} 
          icon={Users} 
        />
        <StatCard 
          label="Recaudado Hoy" 
          value={`$${data.financialSummary.dailyRevenue.toLocaleString()}`} 
          trend="up" 
          trendValue="Hoy" 
          icon={TrendingUp} 
        />
        <StatCard 
          label="Bajas del Mes" 
          value={data.audienceSummary.churnedMembersThisMonth} 
          trend={data.audienceSummary.churnTrend === "Mejoró" ? 'up' : 'down'} 
          trendValue={data.audienceSummary.churnTrend} 
          icon={AlertCircle} 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        <div className="lg:col-span-2 bg-surface-low p-10 rounded-[2.5rem] surface-lift">
          <div className="flex items-center justify-between mb-10">
            <div>
              <h3 className="text-xl font-display font-bold text-text-main flex items-center gap-3">
                <div className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                Composición de Ingresos
              </h3>
              <p className="text-[10px] text-text-secondary uppercase tracking-[0.2em] font-bold mt-2">Abril 2026</p>
            </div>
          </div>
          <div className="h-[350px] w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#1d2024" />
                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#aaabaf', fontSize: 10, fontWeight: 700 }} dy={10} />
                <YAxis axisLine={false} tickLine={false} tick={{ fill: '#aaabaf', fontSize: 10 }} />
                <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(255,255,255,0.02)' }} />
                <Bar dataKey="value" radius={[12, 12, 0, 0]} barSize={60}>
                  {chartData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.color} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="space-y-8">
          <div className="bg-surface-med/30 p-8 rounded-[2rem] border border-surface-med/20 flex flex-col h-fit">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-sm font-display font-black text-text-main uppercase tracking-widest flex items-center gap-2">
                <Clock size={18} className="text-primary" /> Vencimientos
              </h3>
              {expiringCount > 3 && (
                <span className="px-2 py-1 bg-primary text-white text-[10px] font-black rounded-lg shadow-lg shadow-primary/20">
                  {expiringCount}
                </span>
              )}
            </div>
            
            <div className={`space-y-4 pr-2 overflow-y-auto custom-scrollbar ${expiringCount > 3 ? 'max-h-[280px]' : ''}`}>
              {expiringCount === 0 ? (
                <p className="text-xs text-text-secondary italic text-center py-4">Sin alertas próximas</p>
              ) : (
                data.expiringMemberships.map((m) => (
                  <div key={m.memberId} className="flex items-center justify-between p-4 bg-surface-low rounded-2xl border border-transparent hover:border-primary/20 transition-all group flex-shrink-0">
                    <div>
                      <p className="text-sm font-bold text-text-main leading-none mb-1">{m.memberName} {m.memberLastName}</p>
                      <p className="text-[10px] text-error font-bold uppercase tracking-tighter italic">Vence {m.expirationDate}</p>
                    </div>
                    <ArrowUpRight size={16} className="text-text-secondary opacity-0 group-hover:opacity-100" />
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="bg-surface-low p-8 rounded-[2rem] surface-lift">
            <h3 className="text-sm font-display font-black text-text-main uppercase tracking-widest mb-6 flex items-center gap-2">
              <Package size={18} className="text-secondary" /> Top Ventas
            </h3>
            <div className="space-y-6">
              {data.topProducts.map((p, index) => (
                <div key={p.productId} className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-xl bg-surface-high flex items-center justify-center font-display font-black text-primary italic">{index + 1}</div>
                  <div className="flex-1">
                    <p className="text-xs font-bold text-text-main leading-tight mb-1">{p.name}</p>
                    <div className="w-full h-1 bg-surface-high rounded-full overflow-hidden">
                      <div className="h-full bg-secondary rounded-full" style={{ width: `${(p.totalQuantitySold / 10) * 100}%` }} />
                    </div>
                  </div>
                  <span className="text-[10px] font-black text-text-secondary">{p.totalQuantitySold}u.</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
