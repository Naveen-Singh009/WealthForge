import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { roleGuard } from '../../core/guards/role-guard';
import { PortfolioListComponent } from './portfolio-list/portfolio-list';
import { PortfolioCreateComponent } from './portfolio-create/portfolio-create';
import { PortfolioDetailComponent } from './portfolio-detail/portfolio-detail';
import { PortfolioDeleteSuccessComponent } from './portfolio-delete-success/portfolio-delete-success';

const routes: Routes = [
  {
    path: '',
    component: PortfolioListComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
  {
    path: 'create',
    component: PortfolioCreateComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
  {
    path: 'deleted',
    component: PortfolioDeleteSuccessComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR'] },
  },
  {
    path: ':portfolioId',
    component: PortfolioDetailComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PortfolioRoutingModule {}
