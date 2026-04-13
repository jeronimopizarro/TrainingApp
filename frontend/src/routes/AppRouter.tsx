import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '@/routes/guards/ProtectedRoute';

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas publicas: */}
        <Route path="/login" element={<div>Página de Login (En construcción)</div>} />
        <Route path="/unauthorized" element={<div>Acceso Denegado</div>} />

        {/* Rutas protegidas para GYM_ADMIN: */}
        <Route element={<ProtectedRoute allowedRoles={['GYM_ADMIN']} />}>
          <Route path="/admin/dashboard" element={<div>Dashboard Real (En construcción)</div>} />
        </Route>

        {/* Defecto */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
