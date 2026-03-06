import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { StockService } from '../../../core/services/stock.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';
import { Holding, Portfolio } from '../../../shared/models/portfolio.model';
import { Stock } from '../../../shared/models/stock.model';
import { Transaction } from '../../../shared/models/transaction.model';

@Component({
  selector: 'app-investor-dashboard',
  standalone: false,
  templateUrl: './investor-dashboard.html',
  styleUrl: './investor-dashboard.scss',
})
export class InvestorDashboardComponent implements OnInit {
  portfolios: Portfolio[] = [];
  transactions: Transaction[] = [];
  marketOverview: Stock[] = [];
  holdingsBySymbol: Record<string, number | undefined> = {};

  totalBalance = 0;
  totalInvestment = 0;
  pnl = 0;

  constructor(
    private readonly authService: AuthService,
    private readonly portfolioService: PortfolioService,
    private readonly transactionService: TransactionService,
    private readonly stockService: StockService,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    const investorId = this.authService.getCurrentUserId();
    if (!investorId) {
      return;
    }

    this.loadingService.show();

    forkJoin({
      portfolios: this.portfolioService.getInvestorPortfolios(),
      transactions: this.transactionService.getInvestorTransactions(),
      stocks: this.stockService.getAllStocks(),
      overallPerformance: this.portfolioService.getOverallPerformance(),
    }).subscribe({
      next: ({ portfolios, transactions, stocks, overallPerformance }) => {
        this.portfolios = portfolios.data ?? [];
        this.transactions = (transactions.data ?? []).slice(0, 6);
        this.marketOverview = (stocks.data ?? []).slice(0, 5);
        this.loadHoldingSnapshot();

        const overall = overallPerformance.data;
        this.totalBalance = overall?.cashBalance ?? this.portfolios.reduce((sum, item) => sum + (item.balance ?? 0), 0);
        this.totalInvestment = overall?.totalInvested ?? this.portfolios.reduce((sum, item) => sum + (item.balance ?? 0), 0);
        this.pnl = overall?.profitLoss ?? 0;
      },
      error: () => {
        this.toastService.show('error', 'Dashboard Load Failed', 'Unable to load investor dashboard data.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }

  get pnlClass(): string {
    if (this.pnl > 0) {
      return 'profit';
    }

    if (this.pnl < 0) {
      return 'loss';
    }

    return '';
  }

  get pnlDisplay(): string {
    const absValue = Math.abs(this.pnl);
    const formattedValue = new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0,
    }).format(absValue);

    if (this.pnl > 0) {
      return `+${formattedValue}`;
    }

    if (this.pnl < 0) {
      return `-${formattedValue}`;
    }

    return formattedValue;
  }

  private loadHoldingSnapshot(): void {
    if (this.portfolios.length === 0) {
      this.holdingsBySymbol = {};
      return;
    }

    const holdingRequests = this.portfolios.map((portfolio) => this.portfolioService.getPortfolioHoldings(portfolio.id));
    forkJoin(holdingRequests).subscribe({
      next: (responses) => {
        const merged: Record<string, number | undefined> = {};

        responses.forEach((response) => {
          (response.data ?? []).forEach((holding: Holding) => {
            const symbol = (holding.assetSymbol ?? '').toUpperCase();
            if (!symbol) {
              return;
            }
            merged[symbol] = (merged[symbol] ?? 0) + Number(holding.quantity ?? 0);
          });
        });

        this.holdingsBySymbol = merged;
      },
      error: () => {
        this.holdingsBySymbol = {};
      },
    });
  }
}
