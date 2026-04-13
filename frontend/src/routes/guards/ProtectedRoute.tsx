import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { authService } from '@/features/auth/services/auth.service';

// allowedRoles: Lista de roles permitidos para acceder a esta ruta (ej: ['GYM_ADMIN']).
interface ProtectedRouteProps {
  allowedRoles?: string[];
}

/**
 * ProtectedRoute = Componente "Wrapper". 
 * Si las condiciones se cumplen, renderiza el componente interno (Outlet).
 * Si no, redirige al usuario a donde corresponda.
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const location = useLocation(); // Guardamos la ubicación actual para redirigir después del login
  
  const isAuth = authService.isAuthenticated();
  const userRole = authService.getUserRole();

  // ¿Está logueado?
  if (!isAuth) {
    // Si no está logueado, lo mandamos al /login. Cuando se loguee, lo mandamos directo a la pág que intentaba visitar.
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // ¿Tiene el rol necesario?
  if (allowedRoles && !allowedRoles.includes(userRole || '')) {
    return <Navigate to="/unauthorized" replace />;
  }

  // Si pasa todas las validaciones Outlet renderiza la ruta hija
  return <Outlet />;
};
