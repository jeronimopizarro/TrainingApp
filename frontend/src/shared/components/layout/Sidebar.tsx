import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  UserSquare2, 
  CreditCard, 
  Dumbbell, 
  ShoppingBag, 
  History, 
  ShieldCheck, 
  LogOut,
  Trophy,
  X
} from 'lucide-react';
import { authService } from '@/features/auth/services/auth.service';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

/**
 * Sidebar: Barra de navegación fija a la izquierda.
 * Sigue el diseño KINETIC: Superficie baja (#111417) y sin bordes visibles.
 */
export const Sidebar = ({ isOpen, onClose }: SidebarProps) => {
  const role = authService.getUserRole();

  const adminMenuItems = [
    { name: 'Dashboard', icon: <LayoutDashboard size={20} />, path: '/admin/dashboard' },
    { name: 'Socios', icon: <Users size={20} />, path: '/admin/members' },
    { name: 'Staff', icon: <UserSquare2 size={20} />, path: '/admin/staff' },
    { name: 'Membresías', icon: <CreditCard size={20} />, path: '/admin/memberships' },
    { name: 'Ejercicios', icon: <Dumbbell size={20} />, path: '/admin/exercises' },
    { name: 'Productos', icon: <ShoppingBag size={20} />, path: '/admin/products' },
    { name: 'Caja', icon: <History size={20} />, path: '/admin/sales' },
    { name: 'Accesos', icon: <ShieldCheck size={20} />, path: '/admin/access' },
  ];

  const memberMenuItems = [
    { name: 'Inicio', icon: <LayoutDashboard size={20} />, path: '/member/dashboard' },
    { name: 'Mi Rutina', icon: <Dumbbell size={20} />, path: '/member/routine' },
    { name: 'Progresos', icon: <Trophy size={20} />, path: '/member/progress' },
  ];

  const trainerMenuItems = [
    { name: 'Dashboard', icon: <LayoutDashboard size={20} />, path: '/trainer/dashboard' },
    { name: 'Biblioteca Base', icon: <Dumbbell size={20} />, path: '/trainer/routines/bases' },
    { name: 'Mis Alumnos', icon: <Users size={20} />, path: '/trainer/routines/my-created' },
  ];

  const receptionistMenuItems = [
    { name: 'Validar Acceso', icon: <ShieldCheck size={20} />, path: '/receptionist/access' },
    { name: 'Socios', icon: <Users size={20} />, path: '/receptionist/members' },
    { name: 'Caja', icon: <CreditCard size={20} />, path: '/receptionist/sales' },
    { name: 'Historial', icon: <History size={20} />, path: '/receptionist/logs' },
  ];

  const menuItems = role === 'GYM_ADMIN' 
    ? adminMenuItems 
    : role === 'TRAINER' 
      ? trainerMenuItems 
      : role === 'RECEPTIONIST'
        ? receptionistMenuItems
        : memberMenuItems;

  const handleLogout = () => {
    authService.logout();
    window.location.href = '/login';
  };

  return (
    <>
      {/* BACKDROP PARA MOBILE */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-background/60 backdrop-blur-md z-[60] lg:hidden animate-in fade-in duration-300"
          onClick={onClose}
        />
      )}

      {/* ASIDE CONTAINER */}
      <aside className={`
        w-[240px] h-screen bg-surface-low flex flex-col fixed left-0 top-0 z-[70]
        transition-transform duration-300 ease-in-out lg:translate-x-0
        ${isOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        {/* LOGO AREA */}
        <div className="p-6 flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-display font-black tracking-tighter text-text-main italic">
              TrainingApp<span className="text-primary text-3xl">.</span>
            </h1>
            <p className="text-[8px] uppercase tracking-[0.2em] text-text-secondary mt-[2px] font-semibold">
              Control de Alto Rendimiento
            </p>
          </div>
          
          <button 
            onClick={onClose}
            className="p-2 lg:hidden text-text-secondary hover:text-primary transition-colors"
          >
            <X size={24} />
          </button>
        </div>

        {/* NAVIGATION */}
        <nav className="flex-1 px-4 py-2 overflow-y-auto custom-scrollbar">
          <ul className="space-y-1">
            {menuItems.map((item) => (
              <li key={item.name}>
                <NavLink
                  to={item.path}
                  onClick={() => {
                    if (window.innerWidth < 1024) onClose();
                  }}
                  className={({ isActive }) => `
                    flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-300 group
                    ${isActive 
                      ? 'bg-gradient-to-br from-surface-med to-surface-high text-primary shadow-lg' 
                      : 'text-text-secondary hover:bg-surface-med hover:text-text-main'}
                  `}
                >
                  <span className="opacity-80 group-hover:scale-110 transition-transform">
                    {item.icon}
                  </span>
                  <span className="font-sans font-medium text-sm tracking-wide">
                    {item.name}
                  </span>
                  <div className="ml-auto w-1 h-1 rounded-full bg-primary opacity-0 group-[.active]:opacity-100 transition-opacity" />
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        {/* BOTTOM ACTIONS */}
        <div className="p-6 border-t border-surface-med/30 mt-auto">
          <button 
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-4 py-3 text-error/80 hover:text-error hover:bg-error/5 rounded-xl transition-all"
          >
            <LogOut size={20} />
            <span className="text-sm font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>
    </>
  );
};
