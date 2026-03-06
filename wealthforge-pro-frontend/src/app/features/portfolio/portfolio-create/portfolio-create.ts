import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { inject } from '@angular/core';

import { LoadingService } from '../../../core/services/loading.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-portfolio-create',
  standalone: false,
  templateUrl: './portfolio-create.html',
  styleUrl: './portfolio-create.scss',
})
export class PortfolioCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly portfolioService = inject(PortfolioService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  submitting = false;

  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    balance: [1000, [Validators.required, Validators.min(0)]],
  });

  submit(): void {
    if (this.form.invalid || this.submitting) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    this.portfolioService.createPortfolio(this.form.getRawValue()).subscribe({
      next: () => {
        this.toastService.show('success', 'Portfolio Created', 'New portfolio has been created.');
        this.router.navigate(['/investor/portfolio']);
      },
      error: () => {
        this.toastService.show('error', 'Create Failed', 'Unable to create portfolio.');
        this.loadingService.hide();
        this.submitting = false;
      },
      complete: () => {
        this.loadingService.hide();
        this.submitting = false;
      },
    });
  }
}
