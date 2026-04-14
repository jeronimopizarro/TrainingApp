import { useState, useEffect, useCallback } from "react";
import { TransactionCategory, TransactionResponse } from "../types/sale.types";
import { transactionService } from "../services/transaction.service";

export const useTransactions = () => {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentCategory, setCurrentCategory] = useState<TransactionCategory | undefined>(undefined);

  const fetchTransactions = useCallback(async (category?: TransactionCategory) => {
    try {
      setLoading(true);
      setCurrentCategory(category);
      const data = await transactionService.getTransactionHistory(category);
      setTransactions(data);
      setError(null);
    } catch (err) {
      setError("Error al cargar el historial de transacciones");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  return {
    transactions,
    loading,
    error,
    currentCategory,
    refreshTransactions: fetchTransactions,
  };
};
