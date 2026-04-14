export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl?: string;
  isActive: boolean;
  gymId: number;
}

export type StockStatus = 'ALL' | 'LOW_STOCK' | 'OUT_OF_STOCK';

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl?: string;
  gymId?: number;
}

export interface UpdateProductRequest {
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl?: string;
  gymId?: number;
}
