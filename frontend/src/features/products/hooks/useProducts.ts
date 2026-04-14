import { useState, useEffect, useCallback } from 'react';
import { productService } from '../services/product.service';
import { Product, CreateProductRequest, UpdateProductRequest, StockStatus } from '../types/product.types';
import { authService } from '@/features/auth/services/auth.service';

export const useProducts = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeFilter, setActiveFilter] = useState<StockStatus>('ALL');
  const gymId = authService.getUserData()?.gymId;

  const fetchProducts = useCallback(async (filter?: StockStatus) => {
    if (!gymId) return;
    setIsLoading(true);
    setError(null);
    try {
      const statusToFetch = filter || activeFilter;
      const data = await productService.getAll(gymId, statusToFetch === 'ALL' ? undefined : statusToFetch);
      setProducts(data);
    } catch (err: any) {
      setError('Error al cargar el inventario.');
    } finally {
      setIsLoading(false);
    }
  }, [gymId, activeFilter]);

  const setFilter = (filter: StockStatus) => {
    setActiveFilter(filter);
  };

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const searchProducts = async (name: string) => {
    if (!gymId) return;
    if (!name.trim()) {
      return fetchProducts();
    }
    setIsLoading(true);
    try {
      const data = await productService.search(gymId, name);
      setProducts(data);
    } catch (err: any) {
      setError('Error en la búsqueda.');
    } finally {
      setIsLoading(false);
    }
  };

  const createProduct = async (request: CreateProductRequest) => {
    if (!gymId) {
      setError('Sesión inválida: No se encontró el ID del gimnasio.');
      return false;
    }
    setIsLoading(true);
    try {
      await productService.create({ ...request, gymId });
      await fetchProducts();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrar el producto.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const updateProduct = async (id: number, request: UpdateProductRequest) => {
    if (!gymId) {
      setError('Sesión inválida.');
      return false;
    }
    setIsLoading(true);
    try {
      await productService.update(id, { ...request, gymId });
      await fetchProducts();
      return true;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al actualizar el producto.');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const deleteProduct = async (id: number) => {
    try {
      await productService.delete(id);
      await fetchProducts();
      return true;
    } catch (err) {
      setError('No se pudo eliminar el producto.');
      return false;
    }
  };

  return {
    products,
    isLoading,
    error,
    activeFilter,
    setFilter,
    refresh: fetchProducts,
    searchProducts,
    createProduct,
    updateProduct,
    deleteProduct
  };
};
