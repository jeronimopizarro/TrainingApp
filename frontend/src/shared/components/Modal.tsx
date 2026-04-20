import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  size?: 'md' | 'lg' | 'xl';
}

/**
 * Modal Premium: Implementa la profundidad visual Kinetic con backdrop blur.
 */
export const Modal: React.FC<ModalProps> = ({ 
  isOpen, 
  onClose, 
  title, 
  children,
  size = 'md'
}) => {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [onClose]);

  if (!isOpen) return null;

  const sizeClasses = {
    md: 'max-w-3xl',
    lg: 'max-w-5xl',
    xl: 'max-w-7xl'
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
      {/* Backdrop con Blur Dinámico */}
      <div 
        className="absolute inset-0 bg-background/80 backdrop-blur-md animate-in fade-in duration-500"
        onClick={onClose}
      />

      {/* Contenedor del Modal */}
      <div className={`relative w-full ${sizeClasses[size]} bg-surface-low rounded-[1.5rem] sm:rounded-[2.5rem] shadow-2xl border border-white/[0.05] overflow-hidden animate-in zoom-in-95 slide-in-from-bottom-8 duration-500 surface-lift flex flex-col max-h-[90vh]`}>
        
        {/* Header del Modal */}
        <div className="flex items-center justify-between p-6 sm:p-8 border-b border-white/[0.03] flex-shrink-0">
          <div>
            <h2 className="text-xl sm:text-2xl font-display font-black text-text-main tracking-tight italic uppercase">
              {title.split(' ')[0]} <span className="text-primary">{title.split(' ').slice(1).join(' ')}</span>
            </h2>
          </div>
          <button 
            onClick={onClose}
            className="p-2 sm:p-3 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-xl sm:rounded-2xl transition-all duration-300"
          >
            <X size={20} className="sm:hidden" />
            <X size={24} className="hidden sm:block" />
          </button>
        </div>

        {/* Contenido con Scroll si es necesario */}
        <div className="p-6 sm:p-8 overflow-y-auto custom-scrollbar flex-1">
          {children}
        </div>
      </div>
    </div>
  );
};
