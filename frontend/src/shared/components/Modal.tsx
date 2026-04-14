import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

/**
 * Modal Premium: Implementa la profundidad visual Kinetic con backdrop blur.
 */
export const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [onClose]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
      {/* Backdrop con Blur Dinámico */}
      <div 
        className="absolute inset-0 bg-background/60 backdrop-blur-md animate-in fade-in duration-500"
        onClick={onClose}
      />

      {/* Contenedor del Modal */}
      <div className="relative w-full max-w-2xl bg-surface-low rounded-[2.5rem] shadow-2xl border border-white/[0.05] overflow-hidden animate-in zoom-in-95 slide-in-from-bottom-8 duration-500 surface-lift">
        
        {/* Header del Modal */}
        <div className="flex items-center justify-between p-8 border-b border-white/[0.03]">
          <div>
            <h2 className="text-2xl font-display font-black text-text-main tracking-tight italic uppercase">
              {title.split(' ')[0]} <span className="text-primary-dark">{title.split(' ').slice(1).join(' ')}</span>
            </h2>
          </div>
          <button 
            onClick={onClose}
            className="p-3 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-2xl transition-all duration-300"
          >
            <X size={24} />
          </button>
        </div>

        {/* Contenido con Scroll si es necesario */}
        <div className="p-8 max-h-[75vh] overflow-y-auto custom-scrollbar">
          {children}
        </div>
      </div>
    </div>
  );
};
