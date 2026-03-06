import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared-module';
import { AdminPortfolioRoutingModule } from './admin-portfolio-routing-module';
import { AdminPortfolioComponent } from './admin-portfolio/admin-portfolio';

@NgModule({
  declarations: [AdminPortfolioComponent],
  imports: [CommonModule, SharedModule, AdminPortfolioRoutingModule],
})
export class AdminPortfolioModule {}

