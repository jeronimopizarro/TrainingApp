import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';

export const MainLayout = () => {
  return (
    <div className="flex min-h-screen bg-background text-text-main font-sans selection:bg-primary/30 selection:text-primary">
      <Sidebar />
      <main className="flex-1 ml-[260px] flex flex-col min-h-screen">
        <Topbar />
        <section className="p-10 flex-1">
          <Outlet />
        </section>
        <footer className="px-10 py-6 text-[10px] text-text-secondary/30 uppercase tracking-[0.3em] font-semibold text-center sm:text-left">
          © 2026 TRAININGAPP - CONTROL DE ALTO RENDIMIENTO
        </footer>
      </main>
    </div>
  );
};
