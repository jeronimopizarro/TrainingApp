import React from 'react';
import { UserCircle, Menu } from 'lucide-react';
import { authService } from '@/features/auth/services/auth.service';

interface TopbarProps {
  onMenuClick: () => void;
}

/**
 * Topbar: Cabecera superior de TrainingApp
 */
export const Topbar = ({ onMenuClick }: TopbarProps) => {
  const userData = authService.getUserData();
  const userName = userData?.userName || 'Usuario';
  
  const roleLabels: Record<string, string> = {
    'SUPER_ADMIN': 'Super Admin',
    'GYM_ADMIN': 'Administrador Gym',
    'TRAINER': 'Entrenador',
    'RECEPTIONIST': 'Recepción',
    'MEMBER': 'Socio'
  };

  const roleLabel = userData?.role ? (roleLabels[userData.role] || 'Usuario') : 'Usuario';

  return (
    <header className="h-[80px] w-full sticky top-0 z-40 bg-surface-med/50 backdrop-blur-xl px-6 lg:px-10 flex items-center justify-between lg:justify-end">
      {/* Botón de Menú (Solo visible en Mobile) */}
      <button 
        onClick={onMenuClick}
        className="lg:hidden p-3 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-2xl transition-all duration-300"
      >
        <Menu size={24} />
      </button>

      {/* Sección Perfil Usuario (Oculta en Mobile según requerimiento) */}
      <div className="hidden sm:flex items-center gap-6">
        <div className="flex items-center gap-4 cursor-pointer group p-1 pl-4 rounded-2xl hover:bg-surface-high/50 transition-all duration-500">
          <div className="text-right">
            <p className="text-xs font-sans font-bold text-text-main tracking-tight leading-none mb-1.5 group-hover:text-primary transition-colors">
              {userName}
            </p>
            <p className="text-[9px] font-sans font-black text-text-secondary uppercase tracking-[0.2em] italic">
              {roleLabel}
            </p>
          </div>
          
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-br from-primary to-primary-dark flex items-center justify-center text-white shadow-xl shadow-primary/10 group-hover:scale-105 group-hover:rotate-3 transition-all duration-500 border border-white/5 relative overflow-hidden">
            <div className="absolute inset-0 bg-white/10 opacity-0 group-hover:opacity-100 transition-opacity" />
            <UserCircle size={26} strokeWidth={1.5} className="relative z-10" />
          </div>
        </div>
      </div>
    </header>
  );
};
