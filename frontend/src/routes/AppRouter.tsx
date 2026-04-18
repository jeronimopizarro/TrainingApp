import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '@/routes/guards/ProtectedRoute';
import { MainLayout } from '@/shared/components/layout/MainLayout';
import { LoginPage } from '@/features/auth/pages/LoginPage';
import { authService } from '@/features/auth/services/auth.service';
import { AdminDashboardPage } from '@/features/dashboard/pages/AdminDashboardPage';
import { MembersListPage } from '@/features/members/pages/MembersListPage';
import { StaffListPage } from '@/features/staff/pages/StaffListPage';
import { MembershipsPage } from '@/features/memberships/pages/MembershipsPage';
import { ExercisesListPage } from '@/features/exercises/pages/ExercisesListPage';
import { ProductsPage } from '@/features/products/pages/ProductsPage';
import { CashierPage } from '@/features/sales/pages/CashierPage';
import { AccessLogsPage } from '@/features/access/pages/AccessLogsPage';
import { MemberDashboardPage } from '@/features/dashboard/pages/MemberDashboardPage';
import { MyRoutinePage } from '@/features/routines/pages/MyRoutinePage';
import { MemberRoutineBuilderPage } from '@/features/routines/pages/MemberRoutineBuilderPage';
import { WorkoutTrackingPage } from '@/features/tracker/pages/WorkoutTrackingPage';
import { ProgressDashboardPage } from '@/features/tracker/pages/ProgressDashboardPage';
import { TrainerDashboardPage } from '@/features/dashboard/pages/TrainerDashboardPage';
import { BaseRoutinesPage } from '@/features/trainer/pages/BaseRoutinesPage';
import { MyCreatedRoutinesPage } from '@/features/trainer/pages/MyCreatedRoutinesPage';

/**
 * PublicRoute: Evita que usuarios logueados vuelvan al Login.
 * Si ya tienes sesión, te "empuja" a tu dashboard.
 */
const PublicRoute = ({ children }: { children: React.ReactNode }) => {
  const isAuth = authService.isAuthenticated();
  const role = authService.getUserRole();

  if (isAuth) {
    if (role === 'GYM_ADMIN') return <Navigate to="/admin/dashboard" replace />;
    if (role === 'MEMBER') return <Navigate to="/member/dashboard" replace />;
    if (role === 'TRAINER') return <Navigate to="/trainer/dashboard" replace />;
  }
  return <>{children}</>;
};

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* RUTAS PÚBLICAS */}
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
            <Route path="dashboard" element={<AdminDashboardPage />} />
            <Route path="members" element={<MembersListPage />} />
            <Route path="staff" element={<StaffListPage />} />
            <Route path="memberships" element={<MembershipsPage />} />
            <Route path="exercises" element={<ExercisesListPage />} />
            <Route path="products" element={<ProductsPage />} />
            <Route path="sales" element={<CashierPage />} />
            <Route path="access" element={<AccessLogsPage />} />
          </Route>
        </Route>

        {/* ÁREA DE SOCIOS (MEMBER) */}
        <Route path="/member" element={<ProtectedRoute allowedRoles={['MEMBER']} />}>
           <Route element={<MainLayout />}>
           <Route index element={<Navigate to="dashboard" replace />} />
             <Route path="dashboard" element={<MemberDashboardPage />} />
             <Route path="routine" element={<MyRoutinePage />} />
             <Route path="routine/builder" element={<MemberRoutineBuilderPage />} />
             <Route path="progress" element={<ProgressDashboardPage />} />
           </Route>
           {/* El tracker de entrenamiento suele ser pantalla completa para mejor experiencia en el gym */}
           <Route path="workout/:routineId/day/:dayId" element={<WorkoutTrackingPage />} />
        </Route>

        {/* RUTA UNIFICADA DE DETALLES (Accesible por ambos roles) */}
        <Route path="/routines/:id" element={<ProtectedRoute allowedRoles={['MEMBER', 'TRAINER', 'GYM_ADMIN']} />}>
          <Route element={<MainLayout />}>
            <Route index element={<MyRoutinePage />} />
          </Route>
        </Route>

        {/* ÁREA DE ENTRENADORES (TRAINER) */}
        <Route path="/trainer" element={<ProtectedRoute allowedRoles={['TRAINER']} />}>
          <Route element={<MainLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<TrainerDashboardPage />} />
            <Route path="routines/bases" element={<BaseRoutinesPage />} />
            <Route path="routines/my-created" element={<MyCreatedRoutinesPage />} />
            <Route path="routines/builder" element={<MemberRoutineBuilderPage />} />
            <Route path="routines/new-base" element={<MemberRoutineBuilderPage />} />
          </Route>
        </Route>

        {/* REDIRECCIÓN INICIAL INTELIGENTE */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
