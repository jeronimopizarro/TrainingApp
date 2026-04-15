import { jwtDecode } from 'jwt-decode';
import api from '@/shared/services/api';
import { LoginRequest, AuthResponse, DecodedToken, UserSession } from '../types/auth.types';

export const authService = {
  login: async (credentials: LoginRequest): Promise<UserSession> => {
    const { data } = await api.post<AuthResponse>('/auth/login', credentials);
    
    // Extraemos el Rol, GymId y Nombre que inyectamos en el backend
    const decoded = jwtDecode<DecodedToken>(data.token);
    
    // Persistimos los datos en el navegador
    localStorage.setItem('token', data.token);
    localStorage.setItem('user_id', decoded.userId.toString());
    localStorage.setItem('user_role', decoded.role);
    localStorage.setItem('gym_id', decoded.gymId.toString());
    localStorage.setItem('user_name', decoded.userName);

    // Retornamos la sesión completa para que la UI pueda reaccionar
    return {
      token: data.token,
      userId: decoded.userId,
      role: decoded.role,
      gymId: decoded.gymId,
      userName: decoded.userName
    };
  },


  // Valida que el usuario tenga una sesión activa y vigente.
  isAuthenticated: (): boolean => {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      // Verificamos si el token ha expirado (Timestamp en segundos)
      const currentTime = Date.now() / 1000;
      return decoded.exp > currentTime;
    } catch (error) {
      return false;
    }
  },

  // Obtenemos el rol del token
  getUserRole: (): string | null => {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.role;
    } catch (error) {
      return null;
    }
  },

  /**
   * Obtiene la información completa de la sesión actual
   */
  getUserData: (): UserSession | null => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return {
        token,
        userId: decoded.userId,
        role: decoded.role,
        gymId: decoded.gymId,
        userName: decoded.userName
      };
    } catch (error) {
      return null;
    }
  },

    logout: () => {
    localStorage.clear();
  },
};
