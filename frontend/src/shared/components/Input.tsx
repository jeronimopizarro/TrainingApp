import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

/**
 * Componente Input: Diseño sin bordes visibles, basado en capas tonales.
 */
export const Input: React.FC<InputProps> = ({ label, error, icon, className = '', ...props }) => {
  return (
    <div className={`flex flex-col gap-2 w-full ${className}`}>
      {label && (
        <label className="text-[11px] uppercase tracking-[0.2em] text-text-secondary font-bold ml-1 opacity-70">
          {label}
        </label>
      )}
      
      <div className="relative group">
        {icon && (
          <div className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors">
            {icon}
          </div>
        )}
        
        <input
          className={`
            w-full bg-surface-med/50 border-none rounded-2xl py-4 
            ${icon ? 'pl-12' : 'px-6'} pr-6
            text-text-main font-sans text-base
            focus:ring-2 focus:ring-primary/20 focus:bg-surface-med 
            transition-all placeholder:text-text-secondary/30
            ${error ? 'ring-2 ring-error/20 bg-error/5' : ''}
          `}
          {...props}
        />
      </div>

      {error && (
        <span className="text-xs text-error font-bold ml-1 animate-in fade-in slide-in-from-top-1">
          {error}
        </span>
      )}
    </div>
  );
};
