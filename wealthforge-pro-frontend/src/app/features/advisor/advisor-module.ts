import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { AdvisorRoutingModule } from './advisor-routing-module';
import { AdvisorListComponent } from './advisor-list/advisor-list';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [AdvisorListComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    AdvisorRoutingModule,
  ],
})
export class AdvisorModule {}
