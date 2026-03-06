import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { roleGuard } from '../../core/guards/role-guard';
import { AdminPortfolioComponent } from './admin-portfolio/admin-portfolio';

const routes: Routes = [
  {
    path: '',
    component: AdminPortfolioComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminPortfolioRoutingModule {}

