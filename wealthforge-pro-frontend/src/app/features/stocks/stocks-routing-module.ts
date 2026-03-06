import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role-guard';
import { StocksListComponent } from './stocks-list/stocks-list';

const routes: Routes = [
  {
    path: '',
    component: StocksListComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'INVESTOR', 'ADVISOR'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StocksRoutingModule { }
