import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { roleGuard } from '../../core/guards/role-guard';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard';
import { InvestorDashboardComponent } from './investor-dashboard/investor-dashboard';
import { AdvisorDashboardComponent } from './advisor-dashboard/advisor-dashboard';
import { DashboardRedirectComponent } from './dashboard-redirect/dashboard-redirect';

const routes: Routes = [
  { path: '', component: DashboardRedirectComponent },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] },
  },
  {
    path: 'investor',
    component: InvestorDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
  {
    path: 'advisor',
    component: AdvisorDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADVISOR', 'ADMIN'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
