import React from 'react';
import { Package, AlertTriangle, XCircle } from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { StockStatus } from '../types/product.types';

interface StockFilterProps {
  activeFilter: StockStatus;
  onFilterChange: (filter: StockStatus) => void;
}

export const StockFilter: React.FC<StockFilterProps> = ({ activeFilter, onFilterChange }) => {
  const filters: { id: StockStatus; label: string }[] = [
    { id: 'ALL', label: 'Todos' },
    { id: 'LOW_STOCK', label: 'Bajo Stock' },
    { id: 'OUT_OF_STOCK', label: 'Sin Stock' },
  ];

  return (
    <div className="flex items-center gap-2 bg-surface-low p-1.5 rounded-2xl w-fit border border-white/[0.03] mb-8">
      {filters.map((filter) => (
        <Button
          key={filter.id}
          variant={activeFilter === filter.id ? 'primary' : 'ghost'}
          onClick={() => onFilterChange(filter.id)}
          className={`
            px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all
            ${activeFilter !== filter.id ? 'text-text-secondary hover:text-text-main' : ''}
          `}
        >
          {filter.label}
        </Button>
      ))}
    </div>
  );
};

