import api from "../../../shared/services/api";
import { CreateSaleRequest, SaleResponse } from "../types/sale.types";

const SALE_ENDPOINT = "/sales";

export const saleService = {
  processSale: async (request: CreateSaleRequest): Promise<SaleResponse> => {
    const response = await api.post<SaleResponse>(SALE_ENDPOINT, request);
    return response.data;
  },

  getSaleById: async (id: number): Promise<SaleResponse> => {
    const response = await api.get<SaleResponse>(`${SALE_ENDPOINT}/${id}`);
    return response.data;
  },
};
