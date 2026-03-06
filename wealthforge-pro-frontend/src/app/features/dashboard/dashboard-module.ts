import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { DashboardRoutingModule } from './dashboard-routing-module';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard';
import { InvestorDashboardComponent } from './investor-dashboard/investor-dashboard';
import { AdvisorDashboardComponent } from './advisor-dashboard/advisor-dashboard';
import { DashboardRedirectComponent } from './dashboard-redirect/dashboard-redirect';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [
    AdminDashboardComponent,
    InvestorDashboardComponent,
    AdvisorDashboardComponent,
    DashboardRedirectComponent,
  ],
  imports: [CommonModule, ReactiveFormsModule, SharedModule, DashboardRoutingModule],
})
export class DashboardModule {}
