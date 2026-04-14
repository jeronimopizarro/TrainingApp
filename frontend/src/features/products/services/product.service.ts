import api from '@/shared/services/api';
import { Product, CreateProductRequest, UpdateProductRequest } from '../types/product.types';

export const productService = {
  getAll: async (gymId: number, stockStatus?: string): Promise<Product[]> => {
    const response = await api.get<Product[]>(`/products/gym/${gymId}`, {
      params: { stockStatus }
    });
    return response.data;
  },

  getById: async (id: number): Promise<Product> => {
    const response = await api.get<Product>(`/products/${id}`);
    return response.data;
  },

  search: async (gymId: number, name: string): Promise<Product[]> => {
    const response = await api.get<Product[]>(`/products/gym/${gymId}/search`, {
      params: { name }
    });
    return response.data;
  },

  create: async (request: CreateProductRequest): Promise<Product> => {
    const response = await api.post<Product>(`/products`, request);
    return response.data;
  },

  update: async (id: number, request: UpdateProductRequest): Promise<Product> => {
    const response = await api.put<Product>(`/products/${id}`, request);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/products/${id}`);
  }
};
