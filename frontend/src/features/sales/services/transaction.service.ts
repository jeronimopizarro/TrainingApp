import api from "../../../shared/services/api";
import { TransactionCategory, TransactionResponse } from "../types/sale.types";

const TRANSACTION_ENDPOINT = "/transactions";

export const transactionService = {
  getTransactionHistory: async (category?: TransactionCategory): Promise<TransactionResponse[]> => {
    const response = await api.get<TransactionResponse[]>(TRANSACTION_ENDPOINT, {
      params: { category }
    });
    return response.data;
  },
};
