export interface Portfolio {
  id: number;
  investorId: number;
  name: string;
  balance: number;
  totalInvestment?: number;
  profitLoss?: number;
  createdAt?: string;
}

export interface CreatePortfolioRequest {
  name: string;
  balance: number;
}

export interface UpdatePortfolioRequest {
  name: string;
  additionalBalance: number;
}

export interface PortfolioAssetRequest {
  symbol: string;
  quantity: number;
}

export interface Holding {
  id: number;
  assetSymbol: string;
  assetType?: string;
  quantity: number;
  averagePrice?: number;
  currentPrice?: number;
  currentValue?: number;
  profitLoss?: number;
}

export interface AdminPortfolioSummary {
  id?: number;
  investorName?: string;
  investorId?: number;
  portfolioName?: string;
  name?: string;
  totalValue?: number;
  balance?: number;
  createdDate?: string;
  createdAt?: string;
}

export interface PortfolioPerformance {
  portfolioId: number;
  portfolioName: string;
  totalInvested: number;
  currentValue: number;
  profitLoss: number;
  gainPercent: number;
}

export interface OverallPerformance {
  totalPortfolios: number;
  totalInvested: number;
  currentMarketValue: number;
  cashBalance: number;
  netWorth: number;
  profitLoss: number;
  gainPercent: number;
}

export interface DeletePortfolioSummary {
  portfolioId: number;
  portfolioName: string;
  transferredAmount: number;
}
