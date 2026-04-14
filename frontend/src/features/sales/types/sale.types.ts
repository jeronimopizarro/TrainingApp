export enum PaymentMethod {
  CASH = "CASH",
  CARD = "CARD",
  TRANSFER = "TRANSFER",
  VIRTUAL_WALLET = "VIRTUAL_WALLET",
}

export enum TransactionCategory {
  MEMBERSHIP = "MEMBERSHIP",
  PRODUCT = "PRODUCT",
}

export interface TransactionResponse {
  id: number;
  amount: number;
  transactionDate: string;
  paymentMethod: PaymentMethod;
  category: TransactionCategory;
  notes: string;
  gymId: number;
  registeredByAdminId: number;
  subscriptionId?: number;
  saleId?: number;
}

export interface SaleDetailResponse {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface SaleResponse {
  id: number;
  saleDate: string;
  totalAmount: number;
  paymentMethod: PaymentMethod;
  gymId: number;
  registeredByAdminId: number;
  details: SaleDetailResponse[];
}

export interface SaleDetailRequest {
  productId: number;
  quantity: number;
}

export interface CreateSaleRequest {
  paymentMethod: PaymentMethod;
  details: SaleDetailRequest[];
}
