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
  Package
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
    <div className="flex flex-col h-[85vh] md:h-[65vh] max-h-[800px]">
      <div className="flex flex-col md:flex-row flex-1 overflow-hidden">
        {/* Left Side: Product Selection */}
        <div className="w-full md:w-1/2 p-4 flex flex-col border-b md:border-b-0 md:border-r border-white/5 h-[45%] md:h-full">
          <div className="relative mb-4 group">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors" size={20} />
            <input 
              type="text"
              placeholder="Buscar producto..."
              className="w-full bg-surface-high border border-white/10 rounded-2xl py-3 pl-12 pr-4 text-sm text-text-main focus:outline-none focus:border-primary/50 transition-all"
              value={productSearch}
              onChange={(e) => setProductSearch(e.target.value)}
            />
          </div>

          <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar">
            {loadingProducts ? (
              <div className="text-center py-10 animate-pulse text-text-secondary uppercase text-[10px] font-black tracking-widest">Cargando...</div>
            ) : filteredProducts.length === 0 ? (
              <div className="text-center py-10 text-text-secondary italic text-sm">No hay productos</div>
            ) : (
              <div className="grid grid-cols-1 gap-2 pb-4">
                {filteredProducts.map(product => (
                  <div 
                    key={product.id}
                    className="p-2 bg-surface-high/40 rounded-xl border border-white/5 hover:border-primary/30 transition-all cursor-pointer group flex items-center gap-3"
                    onClick={() => addToCart(product)}
                  >
                    <div className="w-10 h-10 rounded-lg bg-surface-high overflow-hidden border border-white/10 flex-shrink-0">
                      {product.imageUrl ? (
                        <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-text-secondary opacity-20">
                          <Package size={18} />
                        </div>
                      )}
                    </div>

                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-bold text-text-main truncate">{product.name}</p>
                      <p className="text-[10px] text-text-secondary font-black uppercase tracking-widest">
                        ${product.price.toLocaleString()}
                      </p>
                    </div>
                    <div className="w-7 h-7 rounded-lg bg-primary/10 flex items-center justify-center text-primary shrink-0">
                      <Plus size={14} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Side: Cart & Details */}
        <div className="w-full md:w-1/2 flex flex-col bg-surface-high/10 h-[55%] md:h-full">
          <div className="p-4 flex-1 flex flex-col overflow-hidden">
            <div className="flex items-center gap-2 mb-3">
              <ShoppingCart size={16} className="text-secondary" />
              <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black">Carrito ({cart.length})</p>
            </div>
            
            <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar space-y-2">
              {cart.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-10 opacity-10">
                  <Package size={32} />
                </div>
              ) : (
                cart.map(item => (
                  <div key={item.productId} className="bg-surface-high/30 p-2 rounded-xl border border-white/5 flex items-center gap-3">
                     <div className="flex-1 min-w-0">
                       <p className="text-xs font-bold text-text-main truncate">{item.name}</p>
                       <p className="text-[10px] text-text-secondary font-black">${item.price.toLocaleString()}</p>
                     </div>
                     <div className="flex items-center bg-surface-high rounded-lg p-1 border border-white/10 scale-90 shrink-0">
                        <button onClick={() => updateQuantity(item.productId, -1)} className="p-1"><Minus size={12} /></button>
                        <span className="w-5 text-center text-xs font-black">{item.quantity}</span>
                        <button onClick={() => updateQuantity(item.productId, 1)} className="p-1"><Plus size={12} /></button>
                     </div>
                     <button onClick={() => removeFromCart(item.productId)} className="p-1.5 text-error/50 hover:text-error transition-colors"><Trash2 size={14} /></button>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="p-4 bg-surface-low border-t border-white/5 space-y-4 shrink-0">
            <div className="grid grid-cols-4 gap-1.5">
              {[
                { id: PaymentMethod.CASH, icon: Banknote, label: 'Efect.' },
                { id: PaymentMethod.CARD, icon: CreditCard, label: 'Tarj.' },
                { id: PaymentMethod.TRANSFER, icon: ArrowUpRight, label: 'Trans.' },
                { id: PaymentMethod.VIRTUAL_WALLET, icon: Wallet, label: 'Digi.' }
              ].map(method => (
                <button
                  key={method.id}
                  onClick={() => setPaymentMethod(method.id)}
                  className={`flex flex-col items-center gap-1 p-2 rounded-xl border transition-all ${
                    paymentMethod === method.id 
                    ? 'bg-primary/10 border-primary text-primary' 
                    : 'bg-surface-high/50 border-white/5 text-text-secondary'
                  }`}
                >
                  <method.icon size={12} />
                  <span className="text-[8px] font-black uppercase tracking-tighter">{method.label}</span>
                </button>
              ))}
            </div>

            <div className="flex items-center justify-between border-t border-white/[0.03] pt-3">
              <div className="min-w-0">
                <p className="text-[9px] uppercase tracking-[0.2em] text-text-secondary font-black">Total</p>
                <p className="text-xl font-display font-black text-text-main truncate">${total.toLocaleString()}</p>
              </div>
              <Button 
                onClick={handleProcessSale}
                isLoading={processingSale}
                disabled={cart.length === 0}
                icon={<CheckCircle2 size={16} />}
                className="px-5 py-2 h-auto text-[10px] font-black uppercase tracking-widest"
              >
                Cobrar
              </Button>
            </div>
            {error && <p className="text-[9px] text-error font-bold text-center italic truncate">! {error}</p>}
          </div>
        </div>
      </div>
    </div>
  );
};
