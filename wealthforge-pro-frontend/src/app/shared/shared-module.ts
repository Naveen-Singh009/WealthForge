import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReusableCardComponent } from './components/reusable-card/reusable-card';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner';
import { ToastContainerComponent } from './components/toast-container/toast-container';
import { EmptyStatePipe } from './pipes/empty-state-pipe';

@NgModule({
  declarations: [
    ReusableCardComponent,
    LoadingSpinnerComponent,
    ToastContainerComponent,
    EmptyStatePipe,
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
  exports: [
    CommonModule,
    RouterModule,
    ReusableCardComponent,
    LoadingSpinnerComponent,
    ToastContainerComponent,
    EmptyStatePipe,
  ],
})
export class SharedModule {}
