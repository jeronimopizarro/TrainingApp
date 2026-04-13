import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Busca el token en el localStoarge, si lo tiene lo añade al header, permitiendo a Spring Securty reconoza al usuario logueado
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    
    // Si tenemos un token, lo enviamos siguiendo el estándar Bearer Token
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Si el servidor dice que no estamos autorizados, limpiamos todo
      localStorage.removeItem('token');
      localStorage.removeItem('user_role');
      // Redirigimos al login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
