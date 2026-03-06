export type TransactionType = 'BUY' | 'SELL';

export interface Transaction {
  id: number;
  portfolioId: number;
  type: TransactionType;
  assetSymbol: string;
  quantity?: number;
  price?: number;
  amount: number;
  timestamp: string;
}

export interface TradeRequest {
  portfolioId: number;
  symbol: string;
  quantity: number;
  price?: number;
}
