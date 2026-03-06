import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { ToastService } from '../../../core/services/toast.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Holding, Portfolio } from '../../../shared/models/portfolio.model';
import { UserRole } from '../../../shared/models/user.model';

@Component({
  selector: 'app-portfolio-detail',
  standalone: false,
  templateUrl: './portfolio-detail.html',
  styleUrl: './portfolio-detail.scss',
})
export class PortfolioDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly portfolioService = inject(PortfolioService);
  private readonly transactionService = inject(TransactionService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  portfolio: Portfolio | null = null;
  holdings: Holding[] = [];
  portfolioId = 0;
  userRole: UserRole | null = null;

  showSellModal = false;
  selectedHolding: Holding | null = null;
  sellSubmitting = false;

  readonly sellForm = this.fb.nonNullable.group({
    symbol: ['', [Validators.required, Validators.minLength(1)]],
    quantity: [1, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('portfolioId');
    this.portfolioId = Number(id);
    this.userRole = this.authService.getRole();
    this.loadPortfolio();
  }

  loadPortfolio(): void {
    if (!this.portfolioId) {
      return;
    }

    this.loadingService.show();

    forkJoin({
      portfolio: this.portfolioService.getPortfolioById(this.portfolioId),
      holdings: this.portfolioService.getPortfolioHoldings(this.portfolioId),
      performance: this.portfolioService.getPortfolioPerformance(this.portfolioId),
    }).subscribe({
      next: ({ portfolio, holdings, performance }) => {
        const basePortfolio = portfolio.data;
        this.portfolio = basePortfolio
          ? {
              ...basePortfolio,
              totalInvestment: performance.data?.totalInvested ?? basePortfolio.totalInvestment,
              profitLoss: performance.data?.profitLoss ?? basePortfolio.profitLoss,
            }
          : null;
        this.holdings = holdings.data ?? [];
      },
      error: () => {
        this.toastService.show('error', 'Portfolio Load Failed', 'Unable to fetch portfolio details.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }

  openSellModal(holding: Holding): void {
    if (this.userRole !== 'INVESTOR' || !holding.assetSymbol || Number(holding.quantity ?? 0) <= 0) {
      return;
    }

    this.selectedHolding = holding;
    this.sellForm.reset({
      symbol: holding.assetSymbol.toUpperCase(),
      quantity: 1,
    });
    this.sellForm.markAsPristine();
    this.sellForm.markAsUntouched();
    this.showSellModal = true;
  }

  closeSellModal(): void {
    if (this.sellSubmitting) {
      return;
    }

    this.showSellModal = false;
    this.selectedHolding = null;
    this.sellForm.reset({ symbol: '', quantity: 1 });
  }

  submitSell(): void {
    if (!this.selectedHolding) {
      return;
    }

    if (this.sellForm.invalid || this.sellSubmitting) {
      this.sellForm.markAllAsTouched();
      return;
    }

    const quantity = Number(this.sellForm.controls.quantity.value);
    const availableQuantity = Number(this.selectedHolding.quantity ?? 0);

    if (quantity > availableQuantity) {
      this.sellForm.controls.quantity.setErrors({ max: true });
      this.sellForm.controls.quantity.markAsTouched();
      return;
    }

    this.sellSubmitting = true;
    this.loadingService.show();

    this.transactionService.sellAsset({
      portfolioId: this.portfolioId,
      symbol: this.sellForm.controls.symbol.value,
      quantity,
    }).subscribe({
      next: () => {
        this.toastService.show('success', 'Sell Order Sent', 'Stock sell request was submitted.');
        this.closeSellModal();
      },
      error: () => {
        this.toastService.show('error', 'Sell Failed', 'Unable to sell stock at this time.');
        this.loadingService.hide();
        this.sellSubmitting = false;
      },
      complete: () => {
        this.loadingService.hide();
        this.sellSubmitting = false;
        this.loadPortfolio();
      },
    });
  }

}
