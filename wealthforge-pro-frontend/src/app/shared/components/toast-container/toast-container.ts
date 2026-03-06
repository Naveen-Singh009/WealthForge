import { Component } from '@angular/core';

import { ToastService } from '../../../core/services/toast.service';
import { inject } from '@angular/core';

@Component({
  selector: 'app-toast-container',
  standalone: false,
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.scss',
})
export class ToastContainerComponent {
  private readonly toastService = inject(ToastService);

  readonly toasts$ = this.toastService.toasts$;

  dismiss(id: number): void {
    this.toastService.dismiss(id);
  }
}
