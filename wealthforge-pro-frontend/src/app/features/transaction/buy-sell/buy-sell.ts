import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';

import { LoadingService } from '../../../core/services/loading.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { StockService } from '../../../core/services/stock.service';
import { ToastService } from '../../../core/services/toast.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Portfolio } from '../../../shared/models/portfolio.model';
import { Stock } from '../../../shared/models/stock.model';
import { TradeRequest } from '../../../shared/models/transaction.model';

@Component({
  selector: 'app-buy-sell',
  standalone: false,
  templateUrl: './buy-sell.html',
  styleUrl: './buy-sell.scss',
})
export class BuySellComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly portfolioService = inject(PortfolioService);
  private readonly stockService = inject(StockService);
  private readonly transactionService = inject(TransactionService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  readonly tradeForm = this.fb.nonNullable.group({
    type: ['BUY' as 'BUY' | 'SELL', [Validators.required]],
    portfolioId: [0, [Validators.required, Validators.min(1)]],
    symbol: ['', [Validators.required, Validators.minLength(1)]],
    quantity: [1, [Validators.required, Validators.min(1)]],
  });

  portfolios: Portfolio[] = [];
  stocks: Stock[] = [];
  filteredStocks: Stock[] = [];
  stockSearch = '';
  submitting = false;

  ngOnInit(): void {
    this.loadTradeContext();
  }

  loadTradeContext(): void {
    this.loadingService.show();

    forkJoin({
      portfolios: this.portfolioService.getInvestorPortfolios(),
      stocks: this.stockService.getAllStocks(),
    }).pipe(
      finalize(() => this.loadingService.hide())
    ).subscribe({
      next: ({ portfolios, stocks }) => {
        this.portfolios = portfolios.data ?? [];
        this.stocks = stocks.data ?? [];
        this.filteredStocks = [...this.stocks];

        if (this.portfolios.length > 0 && this.tradeForm.controls.portfolioId.value < 1) {
          this.tradeForm.controls.portfolioId.setValue(this.portfolios[0].id);
        }
      },
      error: () => {
        this.toastService.show('error', 'Trade Setup Failed', 'Unable to load portfolios or stocks.');
      },
    });
  }

  searchStocks(): void {
    const query = this.stockSearch.trim().toLowerCase();

    if (!query) {
      this.filteredStocks = [...this.stocks];
      return;
    }

    this.filteredStocks = this.stocks.filter((stock) =>
      stock.symbol.toLowerCase().includes(query) ||
      (stock.name ?? '').toLowerCase().includes(query) ||
      (stock.sector ?? '').toLowerCase().includes(query)
    );
  }

  chooseStockForBuy(stock: Stock): void {
    this.tradeForm.patchValue({
      type: 'BUY',
      symbol: stock.symbol.toUpperCase(),
    });
  }

  submit(): void {
    if (this.tradeForm.invalid || this.submitting) {
      this.tradeForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    const payload: TradeRequest = {
      portfolioId: this.tradeForm.controls.portfolioId.value,
      symbol: this.tradeForm.controls.symbol.value.trim().toUpperCase(),
      quantity: this.tradeForm.controls.quantity.value,
    };

    const operation =
      this.tradeForm.controls.type.value === 'BUY'
        ? this.transactionService.buyAsset(payload)
        : this.transactionService.sellAsset(payload);

    operation.pipe(
      finalize(() => {
        this.loadingService.hide();
        this.submitting = false;
      })
    ).subscribe({
      next: (response) => {
        this.toastService.show('success', 'Trade Executed', response.message || `${this.tradeForm.controls.type.value} order placed successfully.`);
      },
      error: (error) => {
        const message =
          error?.error?.message
          ?? error?.error
          ?? error?.message
          ?? 'Unable to execute trade at this time.';
        this.toastService.show('error', 'Trade Failed', String(message));
      },
    });
  }
}
