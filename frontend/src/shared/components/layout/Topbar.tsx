import React from 'react';
import { Search, Bell, Settings2, UserCircle } from 'lucide-react';

/**
 * Topbar: Cabecera superior de TrainingApp.
 */
export const Topbar = () => {
  const userName = localStorage.getItem('user_name') || 'Administrador';

  return (
    <header className="h-[80px] w-full sticky top-0 z-40 bg-surface-med/50 backdrop-blur-xl px-10 flex items-center justify-between">
      <div className="relative group flex-1 max-w-md">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors" size={18} />
        <input 
          type="text" 
          placeholder="Buscar socios, planes, productos..." 
          className="w-full bg-surface-high/50 border-none rounded-2xl py-3 pl-12 pr-6 text-sm text-text-main focus:ring-2 focus:ring-primary/20 transition-all placeholder:text-text-secondary/40"
        />
      </div>

      <div className="flex items-center gap-6 pl-10">
        <button className="relative p-2 text-text-secondary hover:text-text-main hover:bg-surface-high rounded-full transition-all">
          <Bell size={20} />
          <span className="absolute top-2 right-2 w-2 h-2 bg-error rounded-full border-2 border-surface-med" />
        </button>

        <button className="p-2 text-text-secondary hover:text-text-main hover:bg-surface-high rounded-full transition-all">
          <Settings2 size={20} />
        </button>

        <div className="w-[1px] h-6 bg-surface-high mx-2" />

        <div className="flex items-center gap-4 cursor-pointer group p-1 pl-4 rounded-2xl hover:bg-surface-high transition-all">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-sans font-bold text-text-main tracking-tight leading-none mb-1">
              {userName}
            </p>
            <p className="text-[10px] font-sans font-semibold text-text-secondary uppercase tracking-widest opacity-60">
              Administrador Gym
            </p>
          </div>
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-br from-primary to-primary-dark flex items-center justify-center text-white shadow-lg shadow-primary/20">
            <UserCircle size={28} strokeWidth={1.5} />
          </div>
        </div>
      </div>
    </header>
  );
};
