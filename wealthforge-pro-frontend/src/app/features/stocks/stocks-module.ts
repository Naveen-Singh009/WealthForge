import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { StocksRoutingModule } from './stocks-routing-module';
import { StocksListComponent } from './stocks-list/stocks-list';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [StocksListComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SharedModule, StocksRoutingModule],
})
export class StocksModule {}
