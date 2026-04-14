import React from 'react';
import { Input } from '@/shared/components/Input';
import { Button } from '@/shared/components/Button';
import { Save, RotateCcw } from 'lucide-react';

interface UserFormLayoutProps {
  formData: any;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  onReset: () => void;
  onSubmit: (e: React.FormEvent) => void;
  isLoading?: boolean;
  specificFields?: React.ReactNode; 
}

/**
 * UserFormLayout: Estructura base para alta de Socios, Trainers y Staff.
 * Ubicado en shared para garantizar consistencia en todo el sistema.
 */
export const UserFormLayout: React.FC<UserFormLayoutProps> = ({
  formData,
  onChange,
  onReset,
  onSubmit,
  isLoading,
  specificFields
}) => {

  /**
   * handleInternalChange: Intercepta los cambios para aplicar reglas de negocio
   * comunes (como la validación numérica del DNI) antes de actualizar el estado del padre.
   */
  const handleInternalChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    // REGLA GLOBAL: El DNI solo puede contener números
    if (name === 'dni' && value !== '' && !/^\d+$/.test(value)) {
      return;
    }

    onChange(e);
  };

  return (
    <form onSubmit={onSubmit} className="space-y-8">
      {/* Sección: Datos Personales (Común a todos los roles) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Input 
          label="Nombre" 
          name="firstName" 
          value={formData.firstName} 
          onChange={handleInternalChange} 
          placeholder="Ej. Juan" 
          required 
        />
        <Input 
          label="Apellido" 
          name="lastName" 
          value={formData.lastName} 
          onChange={handleInternalChange} 
          placeholder="Ej. Pérez" 
          required 
        />
        <Input 
          label="Email" 
          name="email" 
          type="email" 
          value={formData.email} 
          onChange={handleInternalChange} 
          placeholder="juan.perez@example.com" 
          required 
        />
        <Input 
          label="DNI / Identificación" 
          name="dni" 
          value={formData.dni} 
          onChange={handleInternalChange} 
          placeholder="Solo números" 
          required 
        />
      </div>

      {/* Sección: Campos Específicos (Slot) */}
      {specificFields && (
        <div className="pt-6 border-t border-white/[0.03] grid grid-cols-1 md:grid-cols-2 gap-6">
          {specificFields}
        </div>
      )}

            {/* Nota informativa sobre contraseña */}
      <div className="bg-primary/5 border border-primary/10 rounded-2xl p-4 flex items-start gap-3 mt-2">
        <div className="w-5 h-5 rounded-full bg-primary/20 flex items-center justify-center text-primary text-[10px] font-black shrink-0 mt-0.5">!</div>
        <p className="text-[11px] text-text-secondary leading-relaxed">
          <span className="text-primary font-bold">Nota de Seguridad:</span> La contraseña inicial de acceso para el usuario será su número de <span className="text-text-main font-bold italic">DNI / Identificación</span>. Se recomienda que el usuario la cambie en su primer inicio de sesión.
        </p>
      </div>

      {/* Acciones */}
      <div className="pt-8 flex flex-col sm:flex-row gap-4">
        <Button 
          type="submit" 
          className="flex-1" 
          isLoading={isLoading} 
          icon={<Save size={18} />}
        >
          Guardar Registro
        </Button>
        <Button 
          type="button" 
          variant="secondary" 
          onClick={onReset}
          className="px-8"
          icon={<RotateCcw size={18} />}
        >
          Limpiar
        </Button>
      </div>
    </form>
  );
};
