import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { authGuard } from './core/guards/auth-guard';
import { ShellComponent } from './layout/shell/shell';

const routes: Routes = [
  {
    path: 'login',
    redirectTo: 'auth/login',
    pathMatch: 'full',
  },
  {
    path: 'register',
    redirectTo: 'auth/register',
    pathMatch: 'full',
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth-module').then((m) => m.AuthModule),
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'admin/dashboard',
        redirectTo: 'dashboard/admin',
        pathMatch: 'full',
      },
      {
        path: 'admin/portfolios',
        loadChildren: () =>
          import('./features/admin-portfolio/admin-portfolio-module').then((m) => m.AdminPortfolioModule),
      },
      {
        path: 'admin/stocks',
        redirectTo: 'stocks',
        pathMatch: 'full',
      },
      {
        path: 'admin/advisors',
        redirectTo: 'advisor',
        pathMatch: 'full',
      },
      {
        path: 'investor/dashboard',
        redirectTo: 'dashboard/investor',
        pathMatch: 'full',
      },
      {
        path: 'investor/portfolio',
        redirectTo: 'portfolio',
        pathMatch: 'full',
      },
      {
        path: 'investor/portfolio/create',
        redirectTo: 'portfolio/create',
        pathMatch: 'full',
      },
      {
        path: 'investor/portfolio/:portfolioId',
        redirectTo: 'portfolio/:portfolioId',
        pathMatch: 'full',
      },
      {
        path: 'investor/stocks',
        redirectTo: 'stocks',
        pathMatch: 'full',
      },
      {
        path: 'investor/buy-sell',
        redirectTo: 'transaction/trade',
        pathMatch: 'full',
      },
      {
        path: 'investor/transactions',
        redirectTo: 'transaction',
        pathMatch: 'full',
      },
      {
        path: 'investor/chat-advisor',
        loadChildren: () => import('./features/advisor/advisor-module').then((m) => m.AdvisorModule),
      },
      {
        path: 'advisor/dashboard',
        redirectTo: 'dashboard/advisor',
        pathMatch: 'full',
      },
      {
        path: 'advisor/assigned-investors',
        redirectTo: 'advisor',
        pathMatch: 'full',
      },
      {
        path: 'advisor/portfolio-insights',
        redirectTo: 'dashboard/advisor',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard-module').then((m) => m.DashboardModule),
      },
      {
        path: 'portfolio',
        loadChildren: () => import('./features/portfolio/portfolio-module').then((m) => m.PortfolioModule),
      },
      {
        path: 'stocks',
        loadChildren: () => import('./features/stocks/stocks-module').then((m) => m.StocksModule),
      },
      {
        path: 'advisor',
        loadChildren: () => import('./features/advisor/advisor-module').then((m) => m.AdvisorModule),
      },
      {
        path: 'transaction',
        loadChildren: () => import('./features/transaction/transaction-module').then((m) => m.TransactionModule),
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
