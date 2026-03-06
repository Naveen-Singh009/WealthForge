import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-portfolio-delete-success',
  standalone: false,
  templateUrl: './portfolio-delete-success.html',
  styleUrl: './portfolio-delete-success.scss',
})
export class PortfolioDeleteSuccessComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  transferredAmount = 0;
  portfolioName = '';

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      this.transferredAmount = Number(params.get('amount') ?? 0);
      this.portfolioName = params.get('name') ?? '';
    });
  }
}
