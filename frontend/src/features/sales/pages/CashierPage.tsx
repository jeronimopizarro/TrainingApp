import React, { useState } from 'react';
import { 
  DollarSign, 
  Plus, 
  Search, 
  Receipt,
  ArrowUpRight,
  TrendingUp,
  CreditCard,
  Wallet,
  Banknote
} from 'lucide-react';
import { useTransactions } from '../hooks/useTransactions';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { PaymentMethod, TransactionCategory } from '../types/sale.types';
import { SaleModal } from '../components/SaleModal';

const PaymentMethodBadge = ({ method }: { method: PaymentMethod }) => {
  const configs: Record<PaymentMethod, { label: string; class: string; icon: any }> = {
    [PaymentMethod.CASH]: { label: 'Efectivo', class: 'bg-green-500/10 text-green-400 border-green-500/20', icon: Banknote },
    [PaymentMethod.CARD]: { label: 'Tarjeta', class: 'bg-blue-500/10 text-blue-400 border-blue-500/20', icon: CreditCard },
    [PaymentMethod.TRANSFER]: { label: 'Transferencia', class: 'bg-purple-500/10 text-purple-400 border-purple-500/20', icon: ArrowUpRight },
    [PaymentMethod.VIRTUAL_WALLET]: { label: 'Billetera Virtual', class: 'bg-orange-500/10 text-orange-400 border-orange-500/20', icon: Wallet }
  };

  const config = configs[method];
  const Icon = config.icon;

  return (
    <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest border transition-all duration-500 ${config.class}`}>
      <Icon size={12} />
      {config.label}
    </div>
  );
};

const CategoryBadge = ({ category }: { category: TransactionCategory }) => {
  const configs: Record<TransactionCategory, { label: string; class: string }> = {
    [TransactionCategory.MEMBERSHIP]: { label: 'Membresía', class: 'text-primary' },
    [TransactionCategory.PRODUCT]: { label: 'Producto', class: 'text-secondary' }
  };

  const config = configs[category];

  return (
    <div className={`text-[10px] font-black uppercase tracking-[0.2em] ${config.class}`}>
      {config.label}
    </div>
  );
};

const TransactionRow = ({ transaction, onClick }: { transaction: any; onClick: () => void }) => {
  const date = new Date(transaction.transactionDate);
  const formattedDate = date.toLocaleDateString();
  const formattedTime = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

  return (
    <div 
      onClick={onClick}
      className="group flex items-center gap-6 p-6 bg-surface-low hover:bg-surface-med/50 transition-all cursor-pointer border-b border-white/[0.02] last:border-0 first:rounded-t-[1.5rem] last:rounded-b-[1.5rem]"
    >
      <div className="w-12 h-12 rounded-2xl bg-surface-high flex items-center justify-center group-hover:scale-105 transition-all duration-300 shadow-xl border border-white/5">
        <Receipt size={20} className={transaction.category === TransactionCategory.MEMBERSHIP ? 'text-primary' : 'text-secondary'} />
      </div>
      <div className="flex-1 min-w-0">
        <h3 className="text-sm font-bold text-text-main group-hover:text-primary transition-colors leading-none mb-1">
          {transaction.category === TransactionCategory.MEMBERSHIP ? 'Cobro de Membresía' : 'Venta de Productos'}
        </h3>
        <p className="text-[10px] text-text-secondary opacity-50 font-bold uppercase tracking-widest flex items-center gap-2">
          {formattedDate} • {formattedTime}
        </p>
      </div>
      <div className="hidden lg:block w-32">
        <CategoryBadge category={transaction.category} />
      </div>
      <div className="w-48 flex justify-center">
        <PaymentMethodBadge method={transaction.paymentMethod} />
      </div>
      <div className="w-32 text-right">
        <p className="text-lg font-display font-black text-text-main group-hover:text-primary transition-all tracking-tight">
          ${transaction.amount.toLocaleString()}
        </p>
      </div>
    </div>
  );
};

export const CashierPage = () => {
  const { transactions, loading, error, refreshTransactions, currentCategory } = useTransactions();
  const [isSaleModalOpen, setIsSaleModalOpen] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState<any>(null);
  
  // Correction for toDateString
  const isToday = (dateString: string) => {
    const d = new Date(dateString);
    const today = new Date();
    return d.getDate() === today.getDate() &&
           d.getMonth() === today.getMonth() &&
           d.getFullYear() === today.getFullYear();
  };

  const todayTransactions = transactions.filter(t => isToday(t.transactionDate));
  const todayTotal = todayTransactions.reduce((acc, t) => acc + t.amount, 0);

  const handleFilterChange = (category?: TransactionCategory) => {
    refreshTransactions(category);
  };

  if (loading && transactions.length === 0) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Abriendo Caja...</p>
    </div>
  );

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Finanzas</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Caja y <span className="text-primary-dark">Balance</span>.
          </h1>
        </div>
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => setIsSaleModalOpen(true)}
        >
          Nueva Venta
        </Button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <StatCard 
          label="Recaudado Hoy" 
          value={`$${todayTotal.toLocaleString()}`} 
          icon={TrendingUp} 
          colorClass="text-primary" 
          description={`${todayTransactions.length} transacciones hoy`} 
        />
        <StatCard 
          label="Efectivo en Caja" 
          value={`$${todayTransactions.filter(t => t.paymentMethod === PaymentMethod.CASH).reduce((acc, t) => acc + t.amount, 0).toLocaleString()}`} 
          icon={Banknote} 
          colorClass="text-green-400" 
          description="Total hoy en efectivo" 
        />
        <StatCard 
          label="Pagos Digitales" 
          value={`$${todayTransactions.filter(t => t.paymentMethod !== PaymentMethod.CASH).reduce((acc, t) => acc + t.amount, 0).toLocaleString()}`} 
          icon={CreditCard} 
          colorClass="text-blue-400" 
          description="Tarjeta, Transferencia, Billetera" 
        />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-fit border border-white/[0.03]">
          <Button 
            onClick={() => handleFilterChange(undefined)} 
            variant={!currentCategory ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${!currentCategory ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Todos
          </Button>
          <Button 
            onClick={() => handleFilterChange(TransactionCategory.MEMBERSHIP)} 
            variant={currentCategory === TransactionCategory.MEMBERSHIP ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${currentCategory === TransactionCategory.MEMBERSHIP ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Membresías
          </Button>
          <Button 
            onClick={() => handleFilterChange(TransactionCategory.PRODUCT)} 
            variant={currentCategory === TransactionCategory.PRODUCT ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${currentCategory === TransactionCategory.PRODUCT ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Productos
          </Button>
        </div>
      </div>

      <div className={`bg-surface-low/30 rounded-[1.5rem] border border-white/[0.03] shadow-2xl overflow-hidden transition-all duration-500 ${loading ? 'opacity-40 grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
        {transactions.length === 0 ? (
          <div className="p-20 text-center">
            <Receipt size={48} className="mx-auto text-surface-high mb-4" />
            <p className="text-text-secondary font-bold italic">No hay transacciones registradas</p>
          </div>
        ) : (
          <div className="flex flex-col">
            {transactions.map(transaction => (
              <TransactionRow 
                key={transaction.id} 
                transaction={transaction} 
                onClick={() => setSelectedTransaction(transaction)}
              />
            ))}
          </div>
        )}
      </div>

      {/* Modals will be added here */}
      <Modal 
        isOpen={isSaleModalOpen} 
        onClose={() => setIsSaleModalOpen(false)} 
        title="Nueva Venta de Productos"
        width="max-w-6xl"
      >
        <SaleModal 
          onClose={() => setIsSaleModalOpen(false)} 
          onSuccess={() => {
            setIsSaleModalOpen(false);
            refreshTransactions();
          }} 
        />
      </Modal>

      {selectedTransaction && (
        <Modal 
          isOpen={!!selectedTransaction} 
          onClose={() => setSelectedTransaction(null)} 
          title="Detalle de Transacción"
        >
           <div className="p-6 space-y-6">
              <div className="flex justify-between items-center border-b border-white/5 pb-6">
                <div>
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-1">Monto Total</p>
                  <p className="text-4xl font-display font-black text-primary">${selectedTransaction.amount.toLocaleString()}</p>
                </div>
                <div className="text-right">
                   <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-1">ID Transacción</p>
                   <p className="font-mono font-bold text-text-main">#{selectedTransaction.id}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-8">
                <div>
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Categoría</p>
                  <CategoryBadge category={selectedTransaction.category} />
                </div>
                <div>
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Medio de Pago</p>
                  <PaymentMethodBadge method={selectedTransaction.paymentMethod} />
                </div>
                <div>
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Fecha</p>
                  <p className="text-sm font-bold text-text-main">{new Date(selectedTransaction.transactionDate).toLocaleString()}</p>
                </div>
                <div>
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Registrado por</p>
                  <p className="text-sm font-bold text-text-main italic">Administrador #{selectedTransaction.registeredByAdminId}</p>
                </div>
              </div>

              {selectedTransaction.notes && (
                <div className="bg-surface-high/50 p-4 rounded-2xl border border-white/5">
                  <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Notas</p>
                  <p className="text-xs text-text-main leading-relaxed">{selectedTransaction.notes}</p>
                </div>
              )}

              {selectedTransaction.saleId && (
                <div className="pt-4 border-t border-white/5">
                   <Button 
                    variant="ghost" 
                    fullWidth
                    onClick={() => {
                       // TODO: Navegar a detalle de venta o mostrarlo aquí
                       alert(`Cargar detalle de venta ID: ${selectedTransaction.saleId}`);
                    }}
                   >
                     Ver Detalle de Venta #{selectedTransaction.saleId}
                   </Button>
                </div>
              )}
           </div>
        </Modal>
      )}
    </div>
  );
};
