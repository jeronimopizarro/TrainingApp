import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth.service';
import { LoginRequest } from '../types/auth.types';

export const useLogin = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (credentials: LoginRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const session = await authService.login(credentials);
      
      // REDIRECCIÓN DINÁMICA SEGÚN ROL
      if (session.role === 'GYM_ADMIN') {
        navigate('/admin/dashboard');
      } else if (session.role === 'MEMBER') {
        navigate('/member/dashboard');
      } else if (session.role === 'TRAINER') {
        navigate('/trainer/dashboard');
      } else if (session.role === 'RECEPTIONIST') {
        navigate('/receptionist/access');
      } else {
        // Para otros roles (RECEPTIONIST, etc.)
        navigate('/unauthorized');
      }
    } catch (err: any) {
      const status = err.response?.status;
      let message = 'Error de conexión con el servidor. Inténtalo más tarde.';
      if (status === 401 || status === 403) {
        message = 'Credenciales inválidas. Verifica tu email y contraseña.';
      }
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return { login, isLoading, error };
};
