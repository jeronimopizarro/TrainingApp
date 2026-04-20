import React, { useState, useEffect } from 'react';
import { 
  UserSquare2, 
  UserPlus, 
  Search, 
  Mail, 
  ChevronRight,
  ShieldCheck,
  Dumbbell,
  GraduationCap
} from 'lucide-react';
import { useStaff } from '../hooks/useStaff';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { UserFormLayout } from '@/shared/components/UserFormLayout';
import { Input } from '@/shared/components/Input';
import { StaffMember, StaffRole } from '../types/staff.types';

const INITIAL_FORM_STATE = {
  firstName: '',
  lastName: '',
  email: '',
  dni: '',
  role: 'TRAINER' as StaffRole,
  specialization: ''
};

const RoleBadge = ({ role }: { role: StaffRole }) => {
  const configs: Record<StaffRole, { label: string; class: string; icon: any }> = {
    'TRAINER': { 
      label: 'Entrenador', 
      class: 'bg-primary/10 text-primary border-primary/20 shadow-[0_0_15px_rgba(var(--primary-rgb),0.1)]',
      icon: Dumbbell
    },
    'RECEPTIONIST': { 
      label: 'Recepción', 
      class: 'bg-green-500/10 text-green-400 border-green-500/20 shadow-[0_0_15px_rgba(34,197,94,0.1)]',
      icon: ShieldCheck
    }
  };

  const config = configs[role];
  const Icon = config.icon;

  return (
    <div className={`flex items-center gap-2 px-4 py-1.5 rounded-full text-xs font-black uppercase tracking-widest border transition-all duration-500 ${config.class}`}>
      <Icon size={14} />
      {config.label}
    </div>
  );
};

const StaffRow = ({ member }: { member: StaffMember }) => {
  const initials = `${member.firstName[0]}${member.lastName[0]}`.toUpperCase();

  return (
    <div className="group flex flex-col sm:flex-row sm:items-center gap-4 sm:gap-6 p-4 sm:p-6 bg-surface-low hover:bg-surface-med/50 transition-all cursor-pointer border-b border-white/[0.02] last:border-0 first:rounded-t-[1.5rem] last:rounded-b-[1.5rem]">
      <div className="flex items-center gap-4 min-w-0 flex-1">
        <div className="w-12 h-12 rounded-2xl bg-surface-high flex-shrink-0 flex items-center justify-center font-display font-black text-primary group-hover:scale-105 transition-all duration-300 shadow-xl border border-white/5">
          {initials}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between sm:block">
            <h3 className="text-base font-bold text-text-main group-hover:text-primary transition-colors leading-none mb-1.5 truncate">
              {member.firstName} {member.lastName}
            </h3>
            <div className="sm:hidden scale-75 origin-right">
              <RoleBadge role={member.role} />
            </div>
          </div>
          <p className="text-xs text-text-secondary font-medium truncate flex items-center gap-2">
            <Mail size={12} className="flex-shrink-0" /> {member.email}
          </p>
        </div>
      </div>

      {/* Este contenedor maneja la info extra en móvil (en fila) y se alinea con la fila en PC */}
      <div className="flex items-center justify-between sm:justify-end gap-4 sm:gap-6">
        <div className="hidden lg:block w-32 shrink-0">
          <p className="text-[11px] uppercase tracking-widest text-text-secondary font-bold mb-1">DNI</p>
          <p className="text-sm font-mono font-bold text-text-main">{member.dni}</p>
        </div>
        
        <div className="w-auto sm:w-48 shrink-0">
          <p className="text-sm font-bold text-text-main leading-none mb-1.5 truncate">
            {member.role === 'TRAINER' ? (member as any).specialization : 'Gestión Administrativa'}
          </p>
          <p className="text-[10px] sm:text-xs uppercase tracking-widest text-text-secondary font-black">
            {member.role === 'TRAINER' ? 'Especialidad' : 'Responsabilidad'}
          </p>
        </div>

        <div className="hidden sm:flex w-44 justify-center shrink-0">
          <RoleBadge role={member.role} />
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <button className="p-2 text-text-secondary hover:text-primary transition-colors hover:bg-primary/10 rounded-xl">
            <ChevronRight size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};

export const StaffListPage = () => {
  const { staff, stats, isLoading, error, refresh, registerStaff } = useStaff();
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<string | undefined>(undefined);
  const [isFirstLoad, setIsFirstLoad] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);

  // Marcar que la carga inicial terminó
  useEffect(() => {
    if (!isLoading && isFirstLoad) {
      setIsFirstLoad(false);
    }
  }, [isLoading, isFirstLoad]);

  const filteredStaff = staff.filter(m => 
    `${m.firstName} ${m.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    m.dni.includes(searchTerm) ||
    m.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleFilterChange = (role: string | undefined) => {
    setActiveTab(role);
    refresh(role);
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
    const success = await registerStaff(formData as any);
    if (success) {
      setIsModalOpen(false);
      handleResetForm();
    }
  };

  if (isLoading && isFirstLoad) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Sincronizando Personal...</p>
    </div>
  );

  return (
    <div className={`animate-in fade-in slide-in-from-bottom-4 duration-1000 pb-10 ${isLoading ? 'opacity-40 grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Administración</h2>
          <h1 className="text-4xl sm:text-5xl font-display font-black text-text-main tracking-tight italic">
            Gestión de <span className="text-primary-dark">Personal</span>.
          </h1>
        </div>
        <Button 
          icon={<UserPlus size={18} />} 
          onClick={() => setIsModalOpen(true)}
          className="w-full sm:w-auto"
        >
          Alta de Personal
        </Button>
      </header>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 mb-12">
        <StatCard label="Total Staff" value={stats.total} icon={UserSquare2} colorClass="text-primary" description="Colaboradores" />
        <StatCard label="Entrenadores" value={stats.trainers} icon={Dumbbell} colorClass="text-primary-dark" description="Cuerpo Técnico" />
        <StatCard label="Recepción" value={stats.receptionists} icon={ShieldCheck} colorClass="text-green-400" description="Atención al Socio" />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-full sm:w-fit border border-white/[0.03] overflow-x-auto no-scrollbar">
          <Button 
            onClick={() => handleFilterChange(undefined)} 
            variant={!activeTab ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all whitespace-nowrap ${!activeTab ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Todos
          </Button>
          <Button 
            onClick={() => handleFilterChange('TRAINER')} 
            variant={activeTab === 'TRAINER' ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all whitespace-nowrap ${activeTab === 'TRAINER' ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Entrenadores
          </Button>
          <Button 
            onClick={() => handleFilterChange('RECEPTIONIST')} 
            variant={activeTab === 'RECEPTIONIST' ? 'primary' : 'ghost'}
            className={`flex-1 sm:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all whitespace-nowrap ${activeTab === 'RECEPTIONIST' ? '' : 'text-text-secondary hover:text-text-main'}`}
          >
            Recepción
          </Button>
        </div>

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

      {error && (
        <div className="mb-6 p-4 bg-error/10 border border-error/20 rounded-2xl text-error text-xs font-bold text-center">
          {error}
        </div>
      )}

      <div className={`bg-surface-low/30 rounded-[1.5rem] border border-white/[0.03] shadow-2xl overflow-hidden transition-all duration-500 ${isLoading ? 'opacity-40 grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
        {!isLoading && filteredStaff.length === 0 ? (
          <div className="p-20 text-center">
            <UserSquare2 size={48} className="mx-auto text-surface-high mb-4" />
            <p className="text-text-secondary font-bold italic">No se encontraron miembros del personal</p>
          </div>
        ) : (
          <div className="flex flex-col">
            {filteredStaff.map(member => (
              <StaffRow key={`${member.role}-${member.id}`} member={member} />
            ))}
          </div>
        )}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Alta de Personal">
        <UserFormLayout 
          formData={formData} 
          onChange={handleInputChange} 
          onReset={handleResetForm} 
          onSubmit={handleSubmit} 
          isLoading={isLoading}
          specificFields={
            <>
              <div className="flex flex-col gap-2">
                <label className="text-[10px] uppercase tracking-widest text-text-secondary font-black ml-1">Rol en el Gimnasio</label>
                <select 
                  name="role" 
                  value={formData.role} 
                  onChange={handleInputChange}
                  className="w-full bg-surface-high border border-white/[0.05] rounded-2xl py-4 px-4 text-sm text-text-main focus:outline-none focus:border-primary/50 transition-all appearance-none cursor-pointer"
                >
                  <option value="TRAINER">Entrenador / Coach</option>
                  <option value="RECEPTIONIST">Recepcionista / Admin</option>
                </select>
              </div>
              {formData.role === 'TRAINER' && (
                <Input 
                  label="Especialidad" 
                  name="specialization" 
                  value={formData.specialization} 
                  onChange={handleInputChange} 
                  placeholder="Ej. Musculación, CrossFit..." 
                  icon={<GraduationCap size={18} />} 
                  required
                />
              )}
            </>
          }
        />
      </Modal>
    </div>
  );
};
