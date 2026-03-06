import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role-guard';
import { TransactionHistoryComponent } from './transaction-history/transaction-history';
import { BuySellComponent } from './buy-sell/buy-sell';

const routes: Routes = [
  {
    path: '',
    component: TransactionHistoryComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
  {
    path: 'trade',
    component: BuySellComponent,
    canActivate: [roleGuard],
    data: { roles: ['INVESTOR', 'ADMIN'] },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TransactionRoutingModule {}
