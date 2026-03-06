import { Component, OnInit, inject } from '@angular/core';

import { LoadingService } from '../../../core/services/loading.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { ToastService } from '../../../core/services/toast.service';
import { AdminPortfolioSummary } from '../../../shared/models/portfolio.model';

@Component({
  selector: 'app-admin-portfolio',
  standalone: false,
  templateUrl: './admin-portfolio.html',
  styleUrl: './admin-portfolio.scss',
})
export class AdminPortfolioComponent implements OnInit {
  private readonly portfolioService = inject(PortfolioService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  portfolios: AdminPortfolioSummary[] = [];

  ngOnInit(): void {
    this.loadPortfolios();
  }

  loadPortfolios(): void {
    this.loadingService.show();

    this.portfolioService.getAdminPortfolios().subscribe({
      next: (response) => {
        this.portfolios = response.data ?? [];
      },
      error: () => {
        this.toastService.show('error', 'Portfolio Load Failed', 'Unable to load investor portfolios.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }
}
