import React, { useState, useMemo } from 'react';
import { 
  Search, 
  ShoppingCart, 
  Plus, 
  Minus, 
  Trash2, 
  User, 
  CreditCard,
  Banknote,
  Wallet,
  ArrowUpRight,
  CheckCircle2,
  Package
} from 'lucide-react';
import { useProducts } from '@/features/products/hooks/useProducts';
import { useMembers } from '@/features/members/hooks/useMembers';
import { Button } from '@/shared/components/Button';
import { PaymentMethod, CreateSaleRequest } from '../types/sale.types';
import { useSales } from '../hooks/useSales';

interface CartItem {
  productId: number;
  name: string;
  price: number;
  quantity: number;
  stock: number;
}

interface SaleModalProps {
  onSuccess: () => void;
  onClose: () => void;
}

export const SaleModal = ({ onSuccess, onClose }: SaleModalProps) => {
  const { products, isLoading: loadingProducts, searchProducts } = useProducts();
  const { members } = useMembers(); // Simplified use for now
  const { processSale, loading: processingSale, error } = useSales();

  const [cart, setCart] = useState<CartItem[]>([]);
  const [memberSearch, setMemberSearch] = useState('');
  const [selectedMemberId, setSelectedMemberId] = useState<number | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(PaymentMethod.CASH);
  const [productSearch, setProductSearch] = useState('');

  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(productSearch.toLowerCase()) && p.stock > 0
  );

  const filteredMembers = members.filter(m => 
    `${m.firstName} ${m.lastName}`.toLowerCase().includes(memberSearch.toLowerCase()) ||
    m.dni.includes(memberSearch)
  ).slice(0, 5);

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
        stock: product.stock
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
      memberId: selectedMemberId,
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

  const selectedMember = members.find(m => m.id === selectedMemberId);

  return (
    <div className="flex flex-col h-[80vh] max-h-[700px]">
      <div className="flex flex-1 overflow-hidden">
        {/* Left Side: Product Selection */}
        <div className="w-1/2 p-6 flex flex-col border-r border-white/5">
          <div className="relative mb-6 group">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors" size={18} />
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
              <div className="text-center py-10 animate-pulse text-text-secondary uppercase text-[10px] font-black tracking-widest">Cargando Productos...</div>
            ) : filteredProducts.length === 0 ? (
              <div className="text-center py-10 text-text-secondary italic text-sm">No se encontraron productos con stock</div>
            ) : (
              <div className="grid grid-cols-1 gap-3">
                {filteredProducts.map(product => (
                  <div 
                    key={product.id}
                    className="p-4 bg-surface-high/50 rounded-2xl border border-white/5 hover:border-primary/30 transition-all cursor-pointer group flex items-center justify-between"
                    onClick={() => addToCart(product)}
                  >
                    <div>
                      <p className="text-sm font-bold text-text-main group-hover:text-primary transition-colors">{product.name}</p>
                      <p className="text-[10px] text-text-secondary font-black uppercase tracking-widest mt-1">
                        Stock: <span className={product.stock < 5 ? 'text-error' : 'text-green-400'}>{product.stock}u.</span> • ${product.price.toLocaleString()}
                      </p>
                    </div>
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary opacity-0 group-hover:opacity-100 transition-all scale-75 group-hover:scale-100">
                      <Plus size={16} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Side: Cart & Details */}
        <div className="w-1/2 flex flex-col bg-surface-high/20">
          <div className="p-6 flex-1 flex flex-col overflow-hidden">
            {/* Member Selection */}
            <div className="mb-6">
              <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black mb-2 opacity-50">Socio (Opcional)</p>
              {!selectedMemberId ? (
                <div className="relative group">
                  <User className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary" size={16} />
                  <input 
                    type="text"
                    placeholder="DNI o Nombre..."
                    className="w-full bg-surface-high/50 border border-white/5 rounded-xl py-2 pl-10 pr-4 text-xs text-text-main focus:outline-none focus:border-primary/30 transition-all"
                    value={memberSearch}
                    onChange={(e) => setMemberSearch(e.target.value)}
                  />
                  {memberSearch && filteredMembers.length > 0 && (
                    <div className="absolute top-full left-0 right-0 mt-1 bg-surface-high border border-white/10 rounded-xl shadow-2xl z-10 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
                      {filteredMembers.map(m => (
                        <div 
                          key={m.id}
                          className="p-3 hover:bg-primary/10 cursor-pointer text-xs transition-colors border-b border-white/5 last:border-0"
                          onClick={() => {
                            setSelectedMemberId(m.id);
                            setMemberSearch('');
                          }}
                        >
                          <p className="font-bold text-text-main">{m.firstName} {m.lastName}</p>
                          <p className="text-[10px] text-text-secondary opacity-50">DNI: {m.dni}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ) : (
                <div className="flex items-center justify-between p-3 bg-primary/10 rounded-xl border border-primary/20 animate-in zoom-in-95 duration-200">
                   <div className="flex items-center gap-3">
                     <div className="w-8 h-8 rounded-lg bg-primary/20 flex items-center justify-center text-primary">
                        <User size={16} />
                     </div>
                     <div>
                       <p className="text-xs font-bold text-text-main">{selectedMember?.firstName} {selectedMember?.lastName}</p>
                       <p className="text-[10px] text-text-secondary opacity-50">Socio #{selectedMember?.id}</p>
                     </div>
                   </div>
                   <button 
                    onClick={() => setSelectedMemberId(null)}
                    className="p-2 text-text-secondary hover:text-error transition-colors"
                   >
                     <Trash2 size={14} />
                   </button>
                </div>
              )}
            </div>

            {/* Cart Items */}
            <div className="flex-1 flex flex-col min-h-0">
              <div className="flex items-center gap-2 mb-4">
                <ShoppingCart size={16} className="text-secondary" />
                <p className="text-[10px] uppercase tracking-widest text-text-secondary font-black">Carrito</p>
                <span className="ml-auto text-[10px] font-black bg-secondary/20 text-secondary px-2 py-0.5 rounded-full">{cart.length} items</span>
              </div>
              
              <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar space-y-3">
                {cart.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-12 opacity-20 grayscale">
                    <Package size={48} className="mb-4" />
                    <p className="text-xs italic font-bold">Carrito vacío</p>
                  </div>
                ) : (
                  cart.map(item => (
                    <div key={item.productId} className="bg-surface-high/30 p-3 rounded-xl border border-white/5 flex items-center gap-4 group animate-in slide-in-from-right-2 duration-300">
                       <div className="flex-1 min-w-0">
                         <p className="text-xs font-bold text-text-main truncate">{item.name}</p>
                         <p className="text-[10px] text-text-secondary font-black opacity-50">${item.price.toLocaleString()} c/u</p>
                       </div>
                       <div className="flex items-center bg-surface-high rounded-lg p-1 border border-white/10">
                          <button 
                            onClick={() => updateQuantity(item.productId, -1)}
                            className="p-1 hover:text-primary transition-colors"
                          >
                            <Minus size={12} />
                          </button>
                          <span className="w-8 text-center text-xs font-black text-text-main">{item.quantity}</span>
                          <button 
                            onClick={() => updateQuantity(item.productId, 1)}
                            className={`p-1 transition-colors ${item.quantity >= item.stock ? 'opacity-20 cursor-not-allowed' : 'hover:text-primary'}`}
                            disabled={item.quantity >= item.stock}
                          >
                            <Plus size={12} />
                          </button>
                       </div>
                       <button 
                        onClick={() => removeFromCart(item.productId)}
                        className="text-text-secondary hover:text-error transition-colors"
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
          <div className="p-8 bg-surface-low border-t border-white/5 space-y-6">
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
                  className={`flex flex-col items-center gap-2 p-3 rounded-xl border transition-all ${
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

            <div className="flex items-end justify-between">
              <div>
                <p className="text-[10px] uppercase tracking-[0.2em] text-text-secondary font-black mb-1 opacity-50">Total a Pagar</p>
                <p className="text-4xl font-display font-black text-text-main tracking-tight leading-none">${total.toLocaleString()}</p>
              </div>
              <Button 
                onClick={handleProcessSale}
                isLoading={processingSale}
                disabled={cart.length === 0}
                icon={<CheckCircle2 size={18} />}
                className="px-8 py-4 h-auto text-sm"
              >
                Cobrar Venta
              </Button>
            </div>
            {error && <p className="text-[10px] text-error font-bold text-center animate-shake italic">! {error}</p>}
          </div>
        </div>
      </div>
    </div>
  );
};
