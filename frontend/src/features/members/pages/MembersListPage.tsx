import React, { useState, useEffect } from 'react';
import { 
  Users, 
  UserPlus, 
  Search, 
  Mail, 
  ChevronRight,
  Clock,
  UserCheck,
  Target,
  CalendarDays,
  Zap
} from 'lucide-react';
import { useMembers } from '../hooks/useMembers';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { UserFormLayout } from '@/shared/components/UserFormLayout';
import { Input } from '@/shared/components/Input';
import { RenewMembershipModal } from '@/features/memberships/components/RenewMembershipModal';

const INITIAL_FORM_STATE = {
  firstName: '',
  lastName: '',
  email: '',
  dni: '',
  birthDate: '',
  primaryGoal: ''
};

const StatusBadge = ({ status }: { status?: string }) => {
  const configs: Record<string, { label: string; class: string }> = {
    'ACTIVE': { label: 'Activo', class: 'bg-green-500/10 text-green-400 border-green-500/20 shadow-[0_0_15px_rgba(34,197,94,0.1)]' },
    'EXPIRED': { label: 'Vencido', class: 'bg-error/10 text-error border-error/20 shadow-[0_0_15px_rgba(239,68,68,0.1)]' },
    'CANCELLED': { label: 'Cancelado', class: 'bg-surface-high text-text-secondary border-surface-high/50' },
    'INACTIVE': { label: 'Inactivo', class: 'bg-surface-high text-text-secondary border-surface-high/50' },
    'NONE': { label: 'Sin Plan', class: 'bg-surface-high text-text-secondary border-surface-high/50' }
  };

  const config = configs[status || 'NONE'] || configs['NONE'];

  return (
    <div className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest border transition-all duration-500 ${config.class}`}>
      {config.label}
    </div>
  );
};

const MemberRow = ({ member, onRenew }: { member: any; onRenew: () => void }) => {
  const initials = `${member.firstName[0]}${member.lastName[0]}`.toUpperCase();

  return (
    <div className="group flex items-center gap-6 p-6 bg-surface-low hover:bg-surface-med/50 transition-all border-b border-white/[0.02] last:border-0 first:rounded-t-[1.5rem] last:rounded-b-[1.5rem]">
      <div className="w-12 h-12 rounded-2xl bg-surface-high flex items-center justify-center font-display font-black text-primary group-hover:scale-105 transition-all duration-300 shadow-xl border border-white/5">
        {initials}
      </div>
      <div className="flex-1 min-w-0">
        <h3 className="text-base font-bold text-text-main group-hover:text-primary transition-colors leading-none mb-1.5">
          {member.firstName} {member.lastName}
        </h3>
        <p className="text-xs text-text-secondary font-medium truncate flex items-center gap-2">
          <Mail size={12} /> {member.email}
        </p>
      </div>
      <div className="hidden lg:block w-32">
        <p className="text-[11px] uppercase tracking-widest text-text-secondary font-bold mb-1">DNI</p>
        <p className="text-sm font-mono font-bold text-text-main">{member.dni}</p>
      </div>
      <div className="w-48">
        <p className="text-sm font-bold text-text-main leading-none mb-1.5 truncate">
          {member.planName}
        </p>
        {member.endDate && (
          <p className="text-xs uppercase tracking-widest text-text-secondary font-black">
            Vence: {member.endDate}
          </p>
        )}
      </div>
      <div className="w-32 flex justify-center">
        <StatusBadge status={member.subscriptionStatus} />
      </div>
      <div className="flex items-center gap-2">
        <button 
          onClick={(e) => { e.stopPropagation(); onRenew(); }}
          className="flex items-center gap-2 px-4 py-2 bg-primary/10 text-primary rounded-xl text-[10px] font-black uppercase tracking-widest border border-primary/20 hover:bg-primary hover:text-white transition-all duration-300"
        >
          <Zap size={14} />
          Renovar
        </button>
        <button className="p-2 text-text-secondary hover:text-primary transition-colors hover:bg-primary/10 rounded-xl">
          <ChevronRight size={20} />
        </button>
      </div>
    </div>
  );
};

export const MembersListPage = () => {
  const { members, stats, isLoading, error, refresh, registerMember } = useMembers();
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<string | undefined>(undefined);
  const [isInitialLoad, setIsInitialLoad] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isRenewModalOpen, setIsRenewModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<any>(null);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);

  // Efecto para controlar la carga inicial vs recargas de filtros
  useEffect(() => {
    if (!isLoading && isInitialLoad) {
      setIsInitialLoad(false);
    }
  }, [isLoading, isInitialLoad]);

  const filteredMembers = members.filter(m => 
    `${m.firstName} ${m.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    m.dni.includes(searchTerm) ||
    m.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleFilterChange = (status: string | undefined) => {
    setActiveTab(status);
    refresh(status);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleResetForm = () => {
    setFormData(INITIAL_FORM_STATE);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await registerMember(formData);
    if (success) {
      setIsModalOpen(false);
      handleResetForm();
    }
  };

  const handleOpenRenew = (member: any) => {
    setSelectedMember(member);
    setIsRenewModalOpen(true);
  };

  // Solo mostramos el spinner central en la carga inicial
  if (isLoading && isInitialLoad) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Sincronizando Base de Datos...</p>
    </div>
  );

  if (error) return <div className="p-10 text-error font-bold text-center">{error}</div>;

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Administración</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Gestión de <span className="text-primary-dark">Socios</span>.
          </h1>
        </div>
        <Button 
          icon={<UserPlus size={18} />} 
          onClick={() => setIsModalOpen(true)}
        >
          Alta de Socio
        </Button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <StatCard label="Total Socios" value={stats.total} icon={Users} colorClass="text-primary" description="En base de datos" />
        <StatCard label="Socios Activos" value={stats.active} icon={UserCheck} colorClass="text-green-400" trend="up" trendValue="Suscritos" description="Cuotas al día" />
        <StatCard label="Inactivos / Sin Plan" value={stats.inactive} icon={Clock} colorClass="text-error" trend="down" trendValue="Alertas" description="Requieren atención" />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-fit border border-white/[0.03]">
          <Button 
            onClick={() => handleFilterChange(undefined)} 
            variant={!activeTab ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${!activeTab ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Todos
          </Button>
          <Button 
            onClick={() => handleFilterChange('ACTIVE')} 
            variant={activeTab === 'ACTIVE' ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${activeTab === 'ACTIVE' ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Activos
          </Button>
          <Button 
            onClick={() => handleFilterChange('INACTIVE')} 
            variant={activeTab === 'INACTIVE' ? 'primary' : 'ghost'}
            className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${activeTab === 'INACTIVE' ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Inactivos
          </Button>
        </div>

        <div className="flex flex-col md:flex-row items-center gap-4">
          <div className="relative flex-1 group w-full">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors">
              <Search size={18} />
            </div>
            <input 
              type="text"
              placeholder="Buscar por nombre, DNI o email..."
              className="w-full bg-surface-low border border-white/[0.05] rounded-2xl py-4 pl-12 pr-4 text-sm text-text-main focus:outline-none focus:border-primary/50 focus:ring-4 focus:ring-primary/5 transition-all"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
      </div>

      {/* LISTADO - Solo se aplica opacidad durante las recargas de filtros */}
      <div className={`bg-surface-low/30 rounded-[1.5rem] border border-white/[0.03] shadow-2xl overflow-hidden transition-all duration-500 ${isLoading ? 'grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
        {!isLoading && filteredMembers.length === 0 ? (
          <div className="p-20 text-center">
            <Users size={48} className="mx-auto text-surface-high mb-4" />
            <p className="text-text-secondary font-bold italic">No se encontraron socios</p>
          </div>
        ) : (
          <div className="flex flex-col">
            {filteredMembers.map(member => (
              <MemberRow key={member.id} member={member} onRenew={() => handleOpenRenew(member)} />
            ))}
          </div>
        )}
      </div>

      {/* Modal: Alta de Socio */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Alta de Socio">
        <UserFormLayout formData={formData} onChange={handleInputChange} onReset={handleResetForm} onSubmit={handleSubmit} isLoading={isLoading}
          specificFields={
            <>
              <Input label="Fecha de Nacimiento" name="birthDate" type="date" value={formData.birthDate} onChange={handleInputChange} icon={<CalendarDays size={18} />} />
              <Input label="Objetivo Principal" name="primaryGoal" value={formData.primaryGoal} onChange={handleInputChange} placeholder="Ej. Ganar masa muscular" icon={<Target size={18} />} />
            </>
          }
        />
      </Modal>

      {/* Modal: Renovación de Membresía */}
      <Modal 
        isOpen={isRenewModalOpen} 
        onClose={() => setIsRenewModalOpen(false)} 
        title="Renovar Membresía"
        size="lg"
      >
        {selectedMember && (
          <RenewMembershipModal 
            memberId={selectedMember.id}
            memberName={`${selectedMember.firstName} ${selectedMember.lastName}`}
            onSuccess={() => {
              setIsRenewModalOpen(false);
              refresh(activeTab);
            }}
          />
        )}
      </Modal>
    </div>
  );
};
