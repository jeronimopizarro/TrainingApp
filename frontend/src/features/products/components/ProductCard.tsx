import React from 'react';
import { Package, Edit3, ShoppingBag } from 'lucide-react';
import { Product } from '../types/product.types';

interface ProductCardProps {
  product: Product;
  onEdit: (product: Product) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onEdit }) => {
  const isLowStock = product.stock > 0 && product.stock <= 5;
  const isOutOfStock = product.stock <= 0;

  return (
    <div className="group bg-surface-low border border-white/[0.03] rounded-[1.5rem] overflow-hidden hover:bg-surface-med/50 transition-all shadow-xl flex flex-col">
      <div className="relative h-48 bg-surface-high overflow-hidden">
        {product.imageUrl ? (
          <img 
            src={product.imageUrl} 
            alt={product.name} 
            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" 
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-white/5">
            <ShoppingBag size={64} />
          </div>
        )}
        
        {/* Badge de Precio */}
        <div className="absolute top-4 right-4">
          <span className="px-3 py-1 bg-primary/90 backdrop-blur-md text-white text-xs font-black rounded-full shadow-lg">
            ${product.price.toLocaleString()}
          </span>
        </div>

        {/* Badge de Stock */}
        <div className="absolute top-4 left-4">
          <span className={`px-3 py-1 backdrop-blur-md text-[10px] font-black uppercase tracking-widest rounded-full shadow-lg ${
            isOutOfStock ? 'bg-error text-white' : 
            isLowStock ? 'bg-orange-500 text-white' : 
            'bg-green-500/80 text-white'
          }`}>
            {isOutOfStock ? 'Sin Stock' : `${product.stock} Unidades`}
          </span>
        </div>
      </div>

      <div className="p-6 flex-1 flex flex-col">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-display font-black text-text-main uppercase italic tracking-tight">
            {product.name}
          </h3>
          <button 
            onClick={() => onEdit(product)}
            className="p-2 text-text-secondary hover:text-primary hover:bg-primary/10 rounded-xl transition-all"
          >
            <Edit3 size={18} />
          </button>
        </div>
        <p className="text-xs text-text-secondary font-medium opacity-60 line-clamp-2 mb-6">
          {product.description}
        </p>

        {isLowStock && (
          <div className="mb-6 p-3 bg-orange-500/10 border border-orange-500/20 rounded-xl flex items-center gap-3">
            <div className="w-1.5 h-1.5 rounded-full bg-orange-500 animate-pulse" />
            <p className="text-[10px] font-bold text-orange-500 uppercase tracking-wider">
              Stock crítico: Reponer pronto
            </p>
          </div>
        )}
        
        <div className="mt-auto flex items-center gap-2">
          <Package size={14} className="text-text-secondary opacity-40" />
          <span className="text-[10px] font-bold text-text-secondary opacity-40 uppercase tracking-widest">
            ID: {product.id}
          </span>
          <div className={`ml-auto w-2 h-2 rounded-full ${product.isActive ? 'bg-green-500' : 'bg-error'} shadow-[0_0_8px_rgba(34,197,94,0.3)]`} />
        </div>
      </div>
    </div>
  );
};
