import { useState } from 'react';
import { saleService } from '../services/sale.service';
import { CreateSaleRequest, SaleResponse } from '../types/sale.types';

export const useSales = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const processSale = async (request: CreateSaleRequest): Promise<SaleResponse | null> => {
    setLoading(true);
    setError(null);
    try {
      const response = await saleService.processSale(request);
      return response;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al procesar la venta');
      return null;
    } finally {
      setLoading(false);
    }
  };

  const getSaleDetails = async (id: number): Promise<SaleResponse | null> => {
    setLoading(true);
    setError(null);
    try {
      const response = await saleService.getSaleById(id);
      return response;
    } catch (err: any) {
      setError('Error al obtener los detalles de la venta');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    error,
    processSale,
    getSaleDetails,
  };
};
