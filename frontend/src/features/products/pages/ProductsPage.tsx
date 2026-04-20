import React, { useState, useEffect } from 'react';
import { 
  ShoppingBag, 
  Plus, 
  Search, 
  Package, 
  AlertTriangle,
  DollarSign
} from 'lucide-react';
import { useProducts } from '../hooks/useProducts';
import { Button } from '@/shared/components/Button';
import { StatCard } from '@/shared/components/StatCard';
import { Modal } from '@/shared/components/Modal';
import { ProductCard } from '../components/ProductCard';
import { ProductForm } from '../components/ProductForm';
import { StockFilter } from '../components/StockFilter';
import { Product, CreateProductRequest, StockStatus } from '../types/product.types';

const INITIAL_FORM_STATE: CreateProductRequest = {
  name: '',
  description: '',
  price: 0,
  stock: 0,
  imageUrl: ''
};

export const ProductsPage = () => {
  const { 
    products, 
    isLoading, 
    error, 
    activeFilter,
    setFilter,
    createProduct, 
    updateProduct, 
    deleteProduct, 
    searchProducts 
  } = useProducts();

  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<CreateProductRequest>(INITIAL_FORM_STATE);

  // Control de animación de entrada única
  const [isFirstLoad, setIsFirstLoad] = useState(true);

  // Marcar que la carga inicial terminó
  useEffect(() => {
    if (!isLoading && isFirstLoad) {
      setIsFirstLoad(false);
    }
  }, [isLoading, isFirstLoad]);

  // Cálculos de inventario
  const totalStockValue = products.reduce((acc, p) => acc + (p.price * p.stock), 0);
  const lowStockCount = products.filter(p => p.stock > 0 && p.stock <= 5).length;
  const outOfStockCount = products.filter(p => p.stock <= 0).length;

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    searchProducts(e.target.value);
  };

  const handleFilterChange = (filter: StockStatus) => {
    setFilter(filter);
    setSearchTerm(''); // Limpiamos búsqueda al cambiar filtro
  };

  const handleReset = () => {
    setFormData(INITIAL_FORM_STATE);
    setEditingId(null);
  };

  const handleEdit = (product: Product) => {
    setFormData({
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      imageUrl: product.imageUrl || ''
    });
    setEditingId(product.id);
    setIsModalOpen(true);
  };

  const handleSubmit = async (data: CreateProductRequest) => {
    let success = false;
    if (editingId) {
      success = await updateProduct(editingId, data);
    } else {
      success = await createProduct(data);
    }

    if (success) {
      setIsModalOpen(false);
      handleReset();
    }
  };

  const handleDelete = async () => {
    if (editingId && window.confirm('¿Estás seguro de eliminar este producto?')) {
      const success = await deleteProduct(editingId);
      if (success) {
        setIsModalOpen(false);
        handleReset();
      }
    }
  };

  if (isLoading && isFirstLoad) return (
    <div className="p-20 flex flex-col items-center justify-center gap-6 text-text-secondary animate-pulse">
      <div className="w-12 h-12 border-4 border-primary/10 border-t-primary rounded-full animate-spin" />
      <p className="font-display font-black uppercase tracking-[0.3em] text-[10px]">Cargando Inventario...</p>
    </div>
  );

  return (
    <div className={`pb-10 animate-in fade-in slide-in-from-bottom-4 duration-1000 ${isLoading ? 'grayscale-[50%] pointer-events-none' : 'opacity-100'}`}>
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3">Gestión de Tienda</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic">
            Control de <span className="text-primary-dark">Inventario</span>.
          </h1>
        </div>
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => { handleReset(); setIsModalOpen(true); }}
        >
          Nuevo Producto
        </Button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <StatCard 
          label="Valor Total" 
          value={`$${totalStockValue.toLocaleString()}`} 
          icon={DollarSign} 
          colorClass="text-primary" 
          description="En inventario" 
        />
        <StatCard 
          label="Productos" 
          value={products.length} 
          icon={ShoppingBag} 
          colorClass="text-blue-400" 
          description="Items registrados" 
        />
        <StatCard 
          label="Stock Bajo" 
          value={lowStockCount} 
          icon={AlertTriangle} 
          colorClass="text-orange-400" 
          description="Reponer pronto" 
        />
        <StatCard 
          label="Sin Stock" 
          value={outOfStockCount} 
          icon={Package} 
          colorClass="text-error" 
          description="Items agotados" 
        />
      </div>

      <div className="flex flex-col gap-6 mb-8">
        <div className="relative flex-1 group w-full">
          <div className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors">
            <Search size={18} />
          </div>
          <input 
            type="text"
            placeholder="Buscar producto por nombre..."
            className="w-full bg-surface-low border border-white/[0.05] rounded-2xl py-4 pl-12 pr-4 text-sm text-text-main focus:outline-none focus:border-primary/50 focus:ring-4 focus:ring-primary/5 transition-all"
            value={searchTerm}
            onChange={handleSearch}
          />
        </div>
      </div>

      <StockFilter 
        activeFilter={activeFilter} 
        onFilterChange={handleFilterChange} 
      />

      {error && (
        <div className="mb-10 p-4 bg-error/10 border border-error/20 rounded-2xl text-error text-xs font-bold text-center">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map(product => (
          <ProductCard 
            key={product.id} 
            product={product} 
            onEdit={handleEdit} 
          />
        ))}
      </div>

      {products.length === 0 && !isLoading && (
        <div className="p-20 text-center bg-surface-low rounded-[1.5rem] border border-white/[0.03]">
          <ShoppingBag size={48} className="mx-auto text-surface-high mb-4 opacity-20" />
          <p className="text-text-secondary font-bold italic text-sm">Inventario vacío</p>
        </div>
      )}

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title={editingId ? "Editar Producto" : "Nuevo Producto"}
      >
        <ProductForm 
          initialData={formData}
          isLoading={isLoading}
          isEditing={!!editingId}
          onSubmit={handleSubmit}
          onCancel={() => setIsModalOpen(false)}
          onDelete={handleDelete}
        />
      </Modal>
    </div>
  );
};
