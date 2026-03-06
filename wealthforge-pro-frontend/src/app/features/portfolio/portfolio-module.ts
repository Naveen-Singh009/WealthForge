import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { PortfolioRoutingModule } from './portfolio-routing-module';
import { PortfolioListComponent } from './portfolio-list/portfolio-list';
import { PortfolioCreateComponent } from './portfolio-create/portfolio-create';
import { PortfolioDetailComponent } from './portfolio-detail/portfolio-detail';
import { PortfolioDeleteSuccessComponent } from './portfolio-delete-success/portfolio-delete-success';
import { SharedModule } from '../../shared/shared-module';


@NgModule({
  declarations: [
    PortfolioListComponent,
    PortfolioCreateComponent,
    PortfolioDetailComponent,
    PortfolioDeleteSuccessComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    PortfolioRoutingModule,
  ],
})
export class PortfolioModule {}
