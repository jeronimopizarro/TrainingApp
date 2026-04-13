import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '@/routes/guards/ProtectedRoute';
import { MainLayout } from '@/shared/components/layout/MainLayout';

/**
 * PÁGINAS TEMPORALES (Limpias para el Commit)
 */
const LoginPage = () => (
  <div className="min-h-screen bg-background flex items-center justify-center p-6">
    <div className="max-w-md w-full bg-surface-low p-10 rounded-[2rem] shadow-2xl border border-surface-med/20 text-center">
      <h1 className="text-4xl font-display font-black italic text-text-main mb-2">TrainingApp<span className="text-primary">.</span></h1>
      <p className="text-text-secondary text-xs mb-10 tracking-widest uppercase font-semibold opacity-60">Centro de Control Administrativo</p>
      
      <div className="bg-surface-med/30 p-6 rounded-2xl mb-8 border border-surface-med/20">
        <p className="text-text-secondary text-sm italic">"Página de acceso en desarrollo. <br/> Implementación de lógica de Auth en el próximo paso."</p>
      </div>

      <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
      <p className="text-text-main font-display font-bold text-sm">Cargando Sistema...</p>
    </div>
  </div>
);

const AdminDashboard = () => (
  <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
    <header className="mb-10">
      <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.3em] mb-2">Panel Principal</h2>
      <h1 className="text-4xl font-display font-black text-text-main tracking-tight">Bienvenido a TrainingApp</h1>
      <p className="text-text-secondary mt-2 max-w-xl font-sans font-medium opacity-80">
        Esta es la base operativa del gimnasio. Los módulos de gestión están listos para ser conectados con la lógica de negocio.
      </p>
    </header>
    
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      {[1, 2, 3].map(i => (
        <div key={i} className="h-40 bg-surface-low rounded-[2rem] border border-surface-med/10 p-8 flex flex-col justify-end group hover:bg-surface-med transition-colors cursor-pointer">
          <div className="w-10 h-1 bg-primary/20 rounded-full mb-4 group-hover:w-full transition-all duration-500" />
          <p className="text-[10px] uppercase tracking-widest text-text-secondary font-bold mb-1">Métrica en desarrollo</p>
          <p className="text-3xl font-display font-black text-text-main tracking-tight">-- --</p>
        </div>
      ))}
    </div>
  </div>
);

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/unauthorized" element={
          <div className="h-screen bg-background flex items-center justify-center text-error font-display font-black text-4xl italic">403 | SIN AUTORIZACIÓN</div>
        } />

        <Route element={<ProtectedRoute allowedRoles={['GYM_ADMIN']} />}>
          <Route element={<MainLayout />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/members" element={<div className="font-display font-bold text-2xl">Módulo de Socios</div>} />
            <Route path="/admin/staff" element={<div className="font-display font-bold text-2xl">Módulo de Personal</div>} />
            <Route path="/admin/memberships" element={<div className="font-display font-bold text-2xl">Módulo de Membresías</div>} />
            <Route path="/admin/exercises" element={<div className="font-display font-bold text-2xl">Biblioteca de Ejercicios</div>} />
            <Route path="/admin/products" element={<div className="font-display font-bold text-2xl">Inventario de Productos</div>} />
            <Route path="/admin/sales" element={<div className="font-display font-bold text-2xl">Historial de Ventas</div>} />
            <Route path="/admin/access" element={<div className="font-display font-bold text-2xl">Registro de Accesos</div>} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
