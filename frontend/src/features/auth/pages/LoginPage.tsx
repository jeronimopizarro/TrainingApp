import React, { useState } from 'react';
import { Mail, Lock, Dumbbell } from 'lucide-react';
import { Input } from '@/shared/components/Input';
import { Button } from '@/shared/components/Button';
import { useLogin } from '../hooks/useLogin';

export const LoginPage = () => {
  const { login, isLoading, error } = useLogin();
  
  // Estado local para los campos del formulario
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.email || !formData.password) return;
    
    await login(formData);
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-6 selection:bg-primary/30">

      <div className="max-w-[450px] w-full bg-surface-low p-12 rounded-[2.5rem] shadow-2xl border border-surface-med/10 relative z-10 animate-in fade-in zoom-in-95 duration-500">
        
        {/* LOGO & CABECERA */}
        <header className="text-center mb-12">
          <div className="w-16 h-16 bg-gradient-to-br from-primary to-primary-dark rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-xl shadow-primary/20 rotate-3">
            <Dumbbell className="text-white" size={32} strokeWidth={2.5} />
          </div>
          <h1 className="text-4xl font-display font-black italic text-text-main tracking-tighter mb-2">
            TrainingApp<span className="text-primary text-5xl">.</span>
          </h1>
          <p className="text-text-secondary text-xs tracking-[0.25em] uppercase font-bold opacity-50">
            Control de Alto Rendimiento
          </p>
        </header>

        {/* MENSAJE DE ERROR */}
        {error && (
          <div className="mb-6 p-4 bg-error/10 border-l-4 border-error rounded-xl animate-in slide-in-from-left-2">
            <p className="text-xs text-error font-bold leading-tight">{error}</p>
          </div>
        )}

        {/* FORMULARIO */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <Input 
            label="Correo Electrónico"
            name="email"
            type="email"
            placeholder="ejemplo@gym.com"
            icon={<Mail size={18} />}
            value={formData.email}
            onChange={handleChange}
            required
          />

          <Input 
            label="Contraseña"
            name="password"
            type="password"
            placeholder="••••••••"
            icon={<Lock size={18} />}
            value={formData.password}
            onChange={handleChange}
            required
          />

          <div className="pt-4">
            <Button 
              type="submit" 
              fullWidth 
              isLoading={isLoading}
            >
              Iniciar Sesión
            </Button>
          </div>
        </form>

        <footer className="mt-12 text-center">
          <p className="text-text-secondary text-[10px] uppercase tracking-widest font-bold opacity-30">
            © 2026 TrainingApp Ecosystem
          </p>
        </footer>
      </div>
    </div>
  );
};
