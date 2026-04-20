import React, { useState } from 'react';
import { 
  CreditCard, 
  Plus, 
  Edit3, 
  CalendarDays,
  DollarSign,
  Save,
  RotateCcw,
  Trash2,
  CheckCircle,
  Clock
} from 'lucide-react';
import { useMemberships } from '../hooks/useMemberships';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { Input } from '@/shared/components/Input';
import { MembershipPlan } from '../types/membership.types';

const INITIAL_FORM_STATE = {
  name: '',
  description: '',
  price: 0,
  durationMonths: 1
};

const PlanCard = ({ plan, onEdit }: { plan: MembershipPlan, onEdit: (p: MembershipPlan) => void }) => {
  return (
    <div className="group bg-surface-low border border-white/[0.03] p-6 rounded-[1.5rem] hover:bg-surface-med/50 transition-all shadow-xl">
      <div className="flex justify-between items-start mb-6">
        <div className="p-3 bg-primary/10 rounded-2xl text-primary">
          <CreditCard size={24} />
        </div>
        <button 
          onClick={() => onEdit(plan)}
          className="p-2 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-xl transition-all"
        >
          <Edit3 size={18} />
        </button>
      </div>

      <h3 className="text-lg font-display font-black text-text-main uppercase italic mb-2 tracking-tight">
        {plan.name}
      </h3>
      <p className="text-xs text-text-secondary font-medium mb-6 line-clamp-2 min-h-[32px]">
        {plan.description}
      </p>

      <div className="space-y-3 mb-6">
        <div className="flex items-center gap-3 text-xs text-text-main font-bold">
          <DollarSign size={14} className="text-primary" />
          Precio: <span className="text-primary">${plan.price}</span>
        </div>
        <div className="flex items-center gap-3 text-xs text-text-main font-bold">
          <CalendarDays size={14} className="text-primary" />
          Duración: <span>{plan.durationMonths} {plan.durationMonths === 1 ? 'Mes' : 'Meses'}</span>
        </div>
      </div>

      <div className={`mt-auto pt-4 border-t border-white/[0.03] flex items-center gap-2 text-[10px] font-black uppercase tracking-widest ${plan.active ? 'text-green-400' : 'text-error'}`}>
        <div className={`w-1.5 h-1.5 rounded-full ${plan.active ? 'bg-green-400' : 'bg-error'} animate-pulse`} />
        {plan.active ? 'Plan Activo' : 'Inactivo'}
      </div>
    </div>
  );
};

export const MembershipsPage = () => {
  const { plans, isLoading, error, createPlan, updatePlan, deletePlan } = useMemberships();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ 
      ...prev, 
      [name]: name === 'price' || name === 'durationMonths' ? Number(value) : value 
    }));
  };

  const handleReset = () => {
    setFormData(INITIAL_FORM_STATE);
    setEditingId(null);
  };

  const handleEdit = (plan: MembershipPlan) => {
    setFormData({
      name: plan.name,
      description: plan.description,
      price: plan.price,
      durationMonths: plan.durationMonths
    });
    setEditingId(plan.id);
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    let success = false;
    if (editingId) {
      success = await updatePlan(editingId, formData);
    } else {
      success = await createPlan(formData);
    }

    if (success) {
      setIsModalOpen(false);
      handleReset();
    }
  };

  const handleDelete = async () => {
    if (editingId && window.confirm('¿Estás seguro de eliminar este plan?')) {
      const success = await deletePlan(editingId);
      if (success) {
        setIsModalOpen(false);
        handleReset();
      }
    }
  };

  // Estadísticas para las StatCards
  const activePlans = plans.filter(p => p.active).length;

  if (isLoading && plans.length === 0) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Cargando Catálogo...</p>
    </div>
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Administración</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Catálogo de <span className="text-primary-dark">Planes</span>.
          </h1>
        </div>
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => { handleReset(); setIsModalOpen(true); }}
        >
          Nuevo Plan
        </Button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
        <StatCard label="Total Planes" value={plans.length} icon={CreditCard} colorClass="text-primary" description="Membresías registradas" />
        <StatCard label="Planes Activos" value={activePlans} icon={CheckCircle} colorClass="text-green-400" trend="up" trendValue="Soporte" description="Visibles para socios" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {plans.map(plan => (
          <PlanCard key={plan.id} plan={plan} onEdit={handleEdit} />
        ))}
      </div>

      {plans.length === 0 && !isLoading && (
        <div className="p-20 text-center bg-surface-low rounded-[1.5rem] border border-white/[0.03]">
          <CreditCard size={48} className="mx-auto text-surface-high mb-4 opacity-20" />
          <p className="text-text-secondary font-bold italic text-sm">No hay planes configurados</p>
        </div>
      )}

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title={editingId ? "Editar Membresía" : "Nueva Membresía"}
      >
        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid grid-cols-1 gap-6">
            <Input 
              label="Nombre del Plan" 
              name="name" 
              value={formData.name} 
              onChange={handleInputChange} 
              placeholder="Ej. Plan Black Anual" 
              required 
            />
            <div className="space-y-2">
              <label className="text-[10px] uppercase tracking-widest text-text-secondary font-black ml-1">Descripción</label>
              <textarea 
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                className="w-full bg-surface-high border border-white/[0.05] rounded-2xl py-4 px-4 text-sm text-text-main focus:outline-none focus:border-primary/50 transition-all resize-none min-h-[100px]"
                placeholder="Beneficios y alcance del plan..."
                required
              />
            </div>
            <div className="grid grid-cols-2 gap-6">
              <Input 
                label="Precio ($)" 
                name="price" 
                type="number" 
                value={formData.price} 
                onChange={handleInputChange} 
                placeholder="Ej. 129.99" 
                required 
              />
              <div className="space-y-2">
                <label className="text-[10px] uppercase tracking-widest text-text-secondary font-black ml-1">Duración (Meses)</label>
                <select 
                  name="durationMonths"
                  value={formData.durationMonths}
                  onChange={handleInputChange}
                  className="w-full bg-surface-high border border-white/[0.05] rounded-2xl py-4 px-4 text-sm text-text-main focus:outline-none focus:border-primary/50 transition-all appearance-none cursor-pointer"
                >
                  <option value={1}>1 Mes</option>
                  <option value={3}>3 Meses</option>
                  <option value={6}>6 Meses</option>
                  <option value={12}>12 Meses</option>
                </select>
              </div>
            </div>
          </div>

          <div className="pt-8 border-t border-white/[0.03] flex flex-col sm:flex-row justify-between gap-4">
            {editingId && (
              <Button 
                type="button" 
                variant="secondary" 
                onClick={handleDelete}
                className="text-error border-error/20 hover:bg-error/10"
                icon={<Trash2 size={18} />}
              >
                Eliminar Plan
              </Button>
            )}
            <div className="flex flex-col sm:flex-row gap-4 ml-auto">
              <Button 
                type="button" 
                variant="secondary" 
                onClick={() => { setIsModalOpen(false); handleReset(); }}
                icon={<RotateCcw size={18} />}
              >
                Cancelar
              </Button>
              <Button 
                type="submit" 
                isLoading={isLoading} 
                icon={<Save size={18} />}
              >
                {editingId ? 'Guardar Cambios' : 'Crear Plan'}
              </Button>
            </div>
          </div>
        </form>
      </Modal>
    </div>
  );
};
