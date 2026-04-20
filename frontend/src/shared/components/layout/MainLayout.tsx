import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';

export const MainLayout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);
  const closeSidebar = () => setIsSidebarOpen(false);

  return (
    <div className="flex min-h-screen bg-background text-text-main font-sans selection:bg-primary/30 selection:text-primary overflow-x-hidden">
      <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar} />
      
      <main className="flex-1 lg:ml-[260px] flex flex-col min-h-screen transition-all duration-300">
        <Topbar onMenuClick={toggleSidebar} />
        
        <section className="p-4 sm:p-6 lg:p-10 flex-1 w-full max-w-[100vw] overflow-x-hidden">
          <Outlet />
        </section>

        <footer className="px-6 lg:px-10 py-6 text-[10px] text-text-secondary/30 uppercase tracking-[0.3em] font-semibold text-center lg:text-left">
          © 2026 TRAININGAPP - CONTROL DE ALTO RENDIMIENTO
        </footer>
      </main>
    </div>
  );
};
