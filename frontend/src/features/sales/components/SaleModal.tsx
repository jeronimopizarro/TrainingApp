import React, { useState, useMemo } from 'react';
import { 
  Search, 
  ShoppingCart, 
  Plus, 
  Minus, 
  Trash2, 
  CreditCard,
  Banknote,
  Wallet,
  ArrowUpRight,
  CheckCircle2,
  Package,
  User
} from 'lucide-react';
import { useProducts } from '@/features/products/hooks/useProducts';
import { Button } from '@/shared/components/Button';
import { PaymentMethod, CreateSaleRequest } from '../types/sale.types';
import { useSales } from '../hooks/useSales';

interface CartItem {
  productId: number;
  name: string;
  price: number;
  quantity: number;
  stock: number;
  imageUrl?: string;
}

interface SaleModalProps {
  onSuccess: () => void;
  onClose: () => void;
}

export const SaleModal = ({ onSuccess, onClose }: SaleModalProps) => {
  const { products, isLoading: loadingProducts } = useProducts();
  const { processSale, loading: processingSale, error } = useSales();

  const [cart, setCart] = useState<CartItem[]>([]);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(PaymentMethod.CASH);
  const [productSearch, setProductSearch] = useState('');

  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(productSearch.toLowerCase()) && p.stock > 0
  );

  const addToCart = (product: any) => {
    setCart(prev => {
      const existing = prev.find(item => item.productId === product.id);
      if (existing) {
        if (existing.quantity >= product.stock) return prev;
        return prev.map(item => 
          item.productId === product.id 
            ? { ...item, quantity: item.quantity + 1 } 
            : item
        );
      }
      return [...prev, { 
        productId: product.id, 
        name: product.name, 
        price: product.price, 
        quantity: 1,
        stock: product.stock,
        imageUrl: product.imageUrl
      }];
    });
  };

  const updateQuantity = (productId: number, delta: number) => {
    setCart(prev => prev.map(item => {
      if (item.productId === productId) {
        const newQty = Math.max(1, Math.min(item.stock, item.quantity + delta));
        return { ...item, quantity: newQty };
      }
      return item;
    }));
  };

  const removeFromCart = (productId: number) => {
    setCart(prev => prev.filter(item => item.productId !== productId));
  };

  const total = useMemo(() => 
    cart.reduce((acc, item) => acc + (item.price * item.quantity), 0),
    [cart]
  );

  const handleProcessSale = async () => {
    if (cart.length === 0) return;

    const request: CreateSaleRequest = {
      paymentMethod,
      details: cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    };

    const result = await processSale(request);
    if (result) {
      onSuccess();
    }
  };

  return (
    <div className="flex flex-col h-[65vh] max-h-[600px]">
      <div className="flex flex-1 overflow-hidden">
        {/* Left Side: Product Selection */}
        <div className="w-1/2 p-4 flex flex-col border-r border-white/5">
          <div className="relative mb-4 group">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors" size={20} />
            <input 
              type="text"
              placeholder="Buscar producto..."
              className="w-full bg-surface-high border border-white/10 rounded-2xl py-3 pl-12 pr-4 text-base text-text-main focus:outline-none focus:border-primary/50 transition-all"
              value={productSearch}
              onChange={(e) => setProductSearch(e.target.value)}
            />
          </div>

          <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar">
            {loadingProducts ? (
              <div className="text-center py-10 animate-pulse text-text-secondary uppercase text-xs font-black tracking-widest">Cargando Productos...</div>
            ) : filteredProducts.length === 0 ? (
              <div className="text-center py-10 text-text-secondary italic text-base">No se encontraron productos con stock</div>
            ) : (
              <div className="grid grid-cols-1 gap-3">
                {filteredProducts.map(product => (
                  <div 
                    key={product.id}
                    className="p-2.5 bg-surface-high/50 rounded-2xl border border-white/5 hover:border-primary/30 transition-all cursor-pointer group flex items-center gap-4"
                    onClick={() => addToCart(product)}
                  >
                    {/* Imagen del Producto */}
                    <div className="w-14 h-14 rounded-xl bg-surface-high overflow-hidden border border-white/10 flex-shrink-0 group-hover:border-primary/20 transition-colors">
                      {product.imageUrl ? (
                        <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-text-secondary opacity-30">
                          <Package size={24} />
                        </div>
                      )}
                    </div>

                    <div className="flex-1 min-w-0">
                      <p className="text-base font-bold text-text-main group-hover:text-primary transition-colors truncate">{product.name}</p>
                      <p className="text-xs text-text-secondary font-black uppercase tracking-widest mt-0.5">
                        Stock: <span className={product.stock < 5 ? 'text-error' : 'text-green-400'}>{product.stock}u.</span> • ${product.price.toLocaleString()}
                      </p>
                    </div>
                    <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center text-primary opacity-0 group-hover:opacity-100 transition-all scale-75 group-hover:scale-100 mr-2">
                      <Plus size={18} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Side: Cart & Details */}
        <div className="w-1/2 flex flex-col bg-surface-high/20">
          <div className="p-4 flex-1 flex flex-col overflow-hidden">
            {/* Cart Items */}
            <div className="flex-1 flex flex-col min-h-0">
              <div className="flex items-center gap-2 mb-3">
                <ShoppingCart size={18} className="text-secondary" />
                <p className="text-xs uppercase tracking-widest text-text-secondary font-black">Carrito de Compra</p>
                <span className="ml-auto text-xs font-black bg-secondary/20 text-secondary px-3 py-1 rounded-full">{cart.length} items</span>
              </div>
              
              <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar space-y-2">
                {cart.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-12 opacity-20 grayscale">
                    <Package size={48} className="mb-2" />
                    <p className="text-sm italic font-bold">Carrito vacío</p>
                  </div>
                ) : (
                  cart.map(item => (
                    <div key={item.productId} className="bg-surface-high/30 p-2 rounded-xl border border-white/5 flex items-center gap-3 group animate-in slide-in-from-right-2 duration-300">
                       {/* Miniatura Carrito */}
                       <div className="w-10 h-10 rounded-lg bg-surface-high overflow-hidden border border-white/10 flex-shrink-0">
                          {item.imageUrl ? (
                            <img src={item.imageUrl} alt={item.name} className="w-full h-full object-cover" />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center text-text-secondary opacity-30">
                              <Package size={16} />
                            </div>
                          )}
                       </div>

                       <div className="flex-1 min-w-0">
                         <p className="text-sm font-bold text-text-main truncate">{item.name}</p>
                         <p className="text-xs text-text-secondary font-black opacity-50">${item.price.toLocaleString()} c/u</p>
                       </div>
                       <div className="flex items-center bg-surface-high rounded-lg p-1 border border-white/10 scale-90">
                          <button 
                            onClick={() => updateQuantity(item.productId, -1)}
                            className="p-1 hover:text-primary transition-colors"
                          >
                            <Minus size={14} />
                          </button>
                          <span className="w-6 text-center text-sm font-black text-text-main">{item.quantity}</span>
                          <button 
                            onClick={() => updateQuantity(item.productId, 1)}
                            className={`p-1 transition-colors ${item.quantity >= item.stock ? 'opacity-20 cursor-not-allowed' : 'hover:text-primary'}`}
                            disabled={item.quantity >= item.stock}
                          >
                            <Plus size={14} />
                          </button>
                       </div>
                       <button 
                        onClick={() => removeFromCart(item.productId)}
                        className="p-1.5 text-text-secondary hover:text-error transition-colors"
                       >
                         <Trash2 size={16} />
                       </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

          {/* Footer: Summary & Actions */}
          <div className="p-4 bg-surface-low border-t border-white/5 space-y-4">
            <div className="grid grid-cols-4 gap-2">
              {[
                { id: PaymentMethod.CASH, icon: Banknote, label: 'Efectivo' },
                { id: PaymentMethod.CARD, icon: CreditCard, label: 'Tarjeta' },
                { id: PaymentMethod.TRANSFER, icon: ArrowUpRight, label: 'Transf.' },
                { id: PaymentMethod.VIRTUAL_WALLET, icon: Wallet, label: 'Digital' }
              ].map(method => (
                <button
                  key={method.id}
                  onClick={() => setPaymentMethod(method.id)}
                  className={`flex flex-col items-center gap-1 p-2 rounded-xl border transition-all ${
                    paymentMethod === method.id 
                    ? 'bg-primary/10 border-primary text-primary' 
                    : 'bg-surface-high/50 border-white/5 text-text-secondary hover:border-white/20'
                  }`}
                >
                  <method.icon size={16} />
                  <span className="text-[9px] font-black uppercase tracking-tighter">{method.label}</span>
                </button>
              ))}
            </div>

            <div className="flex items-center justify-between border-t border-white/[0.03] pt-3">
              <div>
                <p className="text-[10px] uppercase tracking-[0.2em] text-text-secondary font-black mb-0.5 opacity-50">Total</p>
                <p className="text-3xl font-display font-black text-text-main tracking-tight leading-none">${total.toLocaleString()}</p>
              </div>
              <Button 
                onClick={handleProcessSale}
                isLoading={processingSale}
                disabled={cart.length === 0}
                icon={<CheckCircle2 size={20} />}
                className="px-6 py-3 h-auto text-base"
              >
                Cobrar
              </Button>
            </div>
            {error && <p className="text-[10px] text-error font-bold text-center animate-shake italic">! {error}</p>}
          </div>
        </div>
      </div>
    </div>
  );
};
