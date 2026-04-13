import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '@/routes/guards/ProtectedRoute';
import { MainLayout } from '@/shared/components/layout/MainLayout';
import { LoginPage } from '@/features/auth/pages/LoginPage';
import { authService } from '@/features/auth/services/auth.service';

/**
 * PublicRoute: Evita que usuarios logueados vuelvan al Login.
 * Si ya tienes sesión, te "empuja" a tu dashboard.
 */
const PublicRoute = ({ children }: { children: React.ReactNode }) => {
  const isAuth = authService.isAuthenticated();
  const role = authService.getUserRole();

  if (isAuth) {
    return <Navigate to={role === 'GYM_ADMIN' ? '/admin/dashboard' : '/member/home'} replace />;
  }
  return <>{children}</>;
};

/**
 * COMPONENTES TEMPORALES
 */
const MemberHome = () => (
  <div className="min-h-screen bg-background p-10 flex flex-col items-center justify-center text-center">
    <div className="w-20 h-20 bg-secondary/20 rounded-full flex items-center justify-center mb-6">
      <span className="text-4xl">👋</span>
    </div>
    <h1 className="text-4xl font-display font-black text-text-main">¡Hola, Socio!</h1>
    <p className="text-text-secondary mt-4 max-w-md">Bienvenido a tu área personal. Muy pronto podrás ver tus rutinas y progresos aquí.</p>
    <button 
      onClick={() => { authService.logout(); window.location.href = '/login'; }}
      className="mt-8 text-primary font-bold hover:underline"
    >
      Cerrar Sesión
    </button>
  </div>
);

const AdminDashboard = () => (
  <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
    <h1 className="text-4xl font-display font-black text-text-main tracking-tight">Panel Administrativo</h1>
    <p className="text-text-secondary mt-2">Bienvenido al control central de TrainingApp.</p>
  </div>
);

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* RUTAS PÚBLICAS (Protegidas por PublicRoute) */}
        <Route path="/login" element={
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        } />
        
        <Route path="/unauthorized" element={
          <div className="h-screen bg-background flex flex-col items-center justify-center gap-4">
            <h1 className="text-error font-display font-black text-6xl italic opacity-20">403</h1>
            <p className="text-text-main font-display font-bold text-2xl">ACCESO RESTRINGIDO</p>
            <p className="text-text-secondary text-sm">Tu cuenta no tiene permisos para esta sección.</p>
            <button onClick={() => window.location.href = '/login'} className="text-primary underline mt-4">Volver al inicio</button>
          </div>
        } />

        {/* ÁREA DE ADMINISTRACIÓN (GYM_ADMIN) */}
        <Route path="/admin" element={<ProtectedRoute allowedRoles={['GYM_ADMIN']} />}>
          <Route element={<MainLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="members" element={<div className="text-text-main font-display font-bold text-2xl">Gestión de Socios</div>} />
            <Route path="staff" element={<div className="text-text-main font-display font-bold text-2xl">Personal</div>} />
            <Route path="memberships" element={<div className="text-text-main font-display font-bold text-2xl">Membresías</div>} />
            <Route path="exercises" element={<div className="text-text-main font-display font-bold text-2xl">Ejercicios</div>} />
            <Route path="products" element={<div className="text-text-main font-display font-bold text-2xl">Productos</div>} />
            <Route path="sales" element={<div className="text-text-main font-display font-bold text-2xl">Ventas</div>} />
            <Route path="access" element={<div className="text-text-main font-display font-bold text-2xl">Accesos</div>} />
          </Route>
        </Route>

        {/* ÁREA DE SOCIOS (MEMBER) */}
        <Route path="/member" element={<ProtectedRoute allowedRoles={['MEMBER']} />}>
          <Route path="home" element={<MemberHome />} />
        </Route>

        {/* REDIRECCIÓN INICIAL INTELIGENTE */}
        <Route path="/" element={<Navigate to="/admin/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
