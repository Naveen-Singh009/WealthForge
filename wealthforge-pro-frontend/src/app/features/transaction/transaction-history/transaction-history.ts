import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Transaction } from '../../../shared/models/transaction.model';

@Component({
  selector: 'app-transaction-history',
  standalone: false,
  templateUrl: './transaction-history.html',
  styleUrl: './transaction-history.scss',
})
export class TransactionHistoryComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  transactions: Transaction[] = [];

  readonly filterForm = this.fb.nonNullable.group({
    fromDate: [''],
    toDate: [''],
  });

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.loadingService.show();

    this.transactionService.getInvestorTransactions().subscribe({
      next: (response) => {
        this.transactions = response.data ?? [];
      },
      error: () => {
        this.toastService.show('error', 'History Error', 'Unable to fetch transaction history.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }
}
