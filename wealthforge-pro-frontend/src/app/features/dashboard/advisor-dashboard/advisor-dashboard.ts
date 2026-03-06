import { Component, OnInit } from '@angular/core';

import { AdvisorService } from '../../../core/services/advisor.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';
import { Advisor } from '../../../shared/models/advisor.model';

@Component({
  selector: 'app-advisor-dashboard',
  standalone: false,
  templateUrl: './advisor-dashboard.html',
  styleUrl: './advisor-dashboard.scss',
})
export class AdvisorDashboardComponent implements OnInit {
  advisors: Advisor[] = [];

  constructor(
    private readonly advisorService: AdvisorService,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadingService.show();

    this.advisorService.getAdvisors().subscribe({
      next: (response) => {
        this.advisors = response.data ?? [];
      },
      error: () => {
        this.toastService.show('error', 'Advisor Data Error', 'Unable to load advisor data.');
      },
      complete: () => {
        this.loadingService.hide();
      },
    });
  }
}
