import React, { useState } from 'react';
import { 
  CreditCard, 
  Banknote, 
  Wallet, 
  ArrowUpRight, 
  CheckCircle2, 
  Zap,
  AlertCircle
} from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { useMemberships } from '../hooks/useMemberships';
import { subscriptionService } from '../services/subscription.service';
import { PaymentMethod } from '../types/membership.types';

interface RenewMembershipModalProps {
  memberId: number;
  memberName: string;
  onSuccess: () => void;
}

export const RenewMembershipModal = ({ memberId, memberName, onSuccess }: RenewMembershipModalProps) => {
  const { plans, isLoading: loadingPlans, error: plansError } = useMemberships();
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(PaymentMethod.CASH);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRenew = async () => {
    if (!selectedPlanId) return;

    setLoading(true);
    setError(null);
    try {
      // Usamos la fecha actual en formato YYYY-MM-DD
      const today = new Date().toISOString().split('T')[0];
      
      await subscriptionService.createSubscription({
        memberId,
        planId: selectedPlanId,
        startDate: today,
        paymentMethod
      });
      
      onSuccess();
    } catch (err: any) {
      console.error('Error renewing membership:', err);
      setError(err.response?.data?.message || 'Error al procesar la renovación. Intente nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  const selectedPlan = plans.find(p => p.id === selectedPlanId);
  const activePlans = plans.filter(p => p.active);

  return (
    <div className="flex flex-col md:flex-row gap-0 overflow-hidden min-h-[500px]">
      {/* Columna Izquierda: Selección de Planes */}
      <div className="flex-1 p-8 border-r border-white/5">
        <div className="flex items-center gap-3 mb-8">
          <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
            <Zap size={20} />
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-[0.2em] text-text-secondary font-black">Planes Disponibles</p>
            <h3 className="text-xl font-display font-black text-text-main tracking-tight">Selecciona una Membresía</h3>
          </div>
        </div>

        {plansError && (
          <div className="p-4 bg-error/10 border border-error/20 rounded-2xl flex items-center gap-3 text-error text-sm mb-6">
            <AlertCircle size={18} />
            <p className="font-bold">{plansError}</p>
          </div>
        )}

        <div className="space-y-3 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
          {loadingPlans ? (
            <div className="space-y-3">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-24 bg-surface-high/20 rounded-2xl animate-pulse border border-white/5" />
              ))}
            </div>
          ) : activePlans.length === 0 ? (
            <div className="py-20 text-center border border-dashed border-white/10 rounded-3xl">
              <p className="text-text-secondary italic text-sm">No hay planes activos disponibles</p>
            </div>
          ) : (
            activePlans.map(plan => (
              <div 
                key={plan.id}
                onClick={() => setSelectedPlanId(plan.id)}
                className={`p-5 rounded-2xl border transition-all cursor-pointer group flex justify-between items-center ${
                  selectedPlanId === plan.id 
                  ? 'bg-primary/10 border-primary shadow-[0_0_25px_rgba(var(--primary-rgb),0.15)]' 
                  : 'bg-surface-high/30 border-white/5 hover:border-white/20 hover:bg-surface-high/50'
                }`}
              >
                <div className="flex-1 min-w-0">
                  <h4 className={`text-base font-bold mb-1 transition-colors ${selectedPlanId === plan.id ? 'text-primary' : 'text-text-main'}`}>
                    {plan.name}
                  </h4>
                  <div className="flex items-center gap-3">
                    <span className="text-[10px] font-black uppercase tracking-tighter bg-surface-high px-2 py-0.5 rounded border border-white/5 text-text-secondary">
                      {plan.durationMonths} {plan.durationMonths === 1 ? 'Mes' : 'Meses'}
                    </span>
                    <p className="text-xs text-text-secondary line-clamp-1">{plan.description}</p>
                  </div>
                </div>
                <div className="text-right ml-4">
                  <p className="text-xl font-display font-black text-text-main">${plan.price.toLocaleString()}</p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Columna Derecha: Confirmación y Pago */}
      <div className="w-full md:w-[380px] bg-surface-high/20 p-8 flex flex-col">
        <div className="flex-1 space-y-8">
          <div className="space-y-1.5">
            <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black">Resumen para:</p>
            <p className="text-2xl font-display font-black text-text-main tracking-tight leading-none">{memberName}</p>
          </div>

          <div className="space-y-4">
            <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black">Método de Pago</p>
            <div className="grid grid-cols-2 gap-2">
              {[
                { id: PaymentMethod.CASH, icon: Banknote, label: 'Efectivo' },
                { id: PaymentMethod.CARD, icon: CreditCard, label: 'Tarjeta' },
                { id: PaymentMethod.TRANSFER, icon: ArrowUpRight, label: 'Transf.' },
                { id: PaymentMethod.VIRTUAL_WALLET, icon: Wallet, label: 'Digital' }
              ].map(method => (
                <button
                  key={method.id}
                  onClick={() => setPaymentMethod(method.id)}
                  className={`flex flex-col items-center gap-2 p-4 rounded-2xl border transition-all ${
                    paymentMethod === method.id 
                    ? 'bg-primary/10 border-primary text-primary' 
                    : 'bg-surface-low border-white/5 text-text-secondary hover:border-white/20'
                  }`}
                >
                  <method.icon size={20} />
                  <span className="text-[10px] font-black uppercase tracking-tighter">{method.label}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="mt-8 space-y-6 pt-8 border-t border-white/5">
          <div className="flex justify-between items-end">
            <div>
              <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-1">Total a cobrar</p>
              <p className="text-4xl font-display font-black text-text-main">${selectedPlan?.price.toLocaleString() || '0'}</p>
            </div>
            {selectedPlan && (
              <div className="text-right">
                <p className="text-[10px] text-primary font-black uppercase tracking-widest">Plan Activo</p>
                <p className="text-xs text-text-secondary font-bold">Hasta {new Date(new Date().setMonth(new Date().getMonth() + selectedPlan.durationMonths)).toLocaleDateString()}</p>
              </div>
            )}
          </div>

          <Button 
            fullWidth
            onClick={handleRenew}
            disabled={!selectedPlanId}
            isLoading={loading}
            icon={<CheckCircle2 size={20} />}
            className="py-5 text-base shadow-xl shadow-primary/10"
          >
            Confirmar Renovación
          </Button>
          
          {error && (
            <div className="p-3 bg-error/10 border border-error/20 rounded-xl flex items-center gap-2 text-error text-[10px] font-bold uppercase animate-shake italic">
              <AlertCircle size={14} />
              {error}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
