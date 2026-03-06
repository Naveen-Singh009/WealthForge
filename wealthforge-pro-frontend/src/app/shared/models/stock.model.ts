export interface Stock {
  id: number;
  symbol: string;
  name?: string;
  sector?: string;
  currentPrice: number;
  availableQuantity?: number;
  changePercent?: number;
  volume?: number;
}

export interface CreateStockRequest {
  symbol: string;
  name: string;
  sector: string;
  currentPrice: number;
  availableQuantity: number;
}

export interface UpdateStockRequest {
  symbol: string;
  name: string;
  sector: string;
  currentPrice: number;
  availableQuantity: number;
}
