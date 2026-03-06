import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { roleGuard } from '../../core/guards/role-guard';
import { AdvisorListComponent } from './advisor-list/advisor-list';

const routes: Routes = [
  {
    path: '',
    component: AdvisorListComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADVISOR', 'ADMIN'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdvisorRoutingModule {}
