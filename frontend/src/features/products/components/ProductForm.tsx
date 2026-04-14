import React, { useState } from 'react';
import { 
  Save, 
  RotateCcw, 
  Trash2, 
  Image as ImageIcon, 
  DollarSign,
  Package
} from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { CreateProductRequest } from '../types/product.types';

interface ProductFormProps {
  initialData: CreateProductRequest;
  isLoading: boolean;
  isEditing: boolean;
  onSubmit: (data: CreateProductRequest) => Promise<void>;
  onCancel: () => void;
  onDelete?: () => void;
}

export const ProductForm: React.FC<ProductFormProps> = ({
  initialData,
  isLoading,
  isEditing,
  onSubmit,
  onCancel,
  onDelete
}) => {
  const [formData, setFormData] = useState<CreateProductRequest>(initialData);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    // Manejo de valores numéricos
    if (name === 'price' || name === 'stock') {
      const numValue = value === '' ? 0 : parseFloat(value);
      setFormData(prev => ({ ...prev, [name]: isNaN(numValue) ? 0 : numValue }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.price < 0 || formData.stock < 0) {
      alert('El precio y el stock no pueden ser negativos.');
      return;
    }
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="space-y-6">
          <Input 
            label="Nombre del Producto" 
            name="name" 
            value={formData.name} 
            onChange={handleInputChange} 
            placeholder="Ej. Proteína Whey 1kg" 
            required 
          />
          <div className="space-y-2">
            <label className="text-[10px] uppercase tracking-widest text-text-secondary font-black ml-1">Descripción</label>
            <textarea 
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              className="w-full bg-surface-high border border-white/[0.05] rounded-2xl py-4 px-4 text-sm text-text-main focus:outline-none focus:border-primary/50 transition-all resize-none min-h-[120px]"
              placeholder="Detalles del producto, marca, sabor..."
              required
            />
          </div>
        </div>

        <div className="space-y-6">
          <div className="grid grid-cols-2 gap-4">
            <Input 
              label="Precio ($)" 
              name="price" 
              type="number"
              step="0.01"
              value={formData.price} 
              onChange={handleInputChange} 
              placeholder="0.00" 
              icon={<DollarSign size={18} />}
              required 
            />
            <Input 
              label="Stock Inicial" 
              name="stock" 
              type="number"
              value={formData.stock} 
              onChange={handleInputChange} 
              placeholder="0" 
              icon={<Package size={18} />}
              required 
            />
          </div>
          <Input 
            label="URL de Imagen (Opcional)" 
            name="imageUrl" 
            value={formData.imageUrl} 
            onChange={handleInputChange} 
            placeholder="https://..." 
            icon={<ImageIcon size={18} />}
          />
        </div>
      </div>

      <div className="pt-8 border-t border-white/[0.03] flex flex-col sm:flex-row justify-between gap-4">
        {isEditing && onDelete && (
          <Button 
            type="button" 
            variant="secondary" 
            onClick={onDelete}
            className="text-error border-error/20 hover:bg-error/10"
            icon={<Trash2 size={18} />}
          >
            Eliminar
          </Button>
        )}
        <div className="flex flex-col sm:flex-row gap-4 ml-auto">
          <Button 
            type="button" 
            variant="secondary" 
            onClick={onCancel}
            icon={<RotateCcw size={18} />}
          >
            Cancelar
          </Button>
          <Button 
            type="submit" 
            isLoading={isLoading} 
            icon={<Save size={18} />}
          >
            {isEditing ? 'Guardar Cambios' : 'Registrar Producto'}
          </Button>
        </div>
      </div>
    </form>
  );
};
