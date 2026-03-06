import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface ToastMessage {
  id: number;
  type: ToastType;
  title: string;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private readonly toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
  readonly toasts$ = this.toastsSubject.asObservable();

  show(type: ToastType, title: string, message: string): void {
    const toast: ToastMessage = {
      id: Date.now() + Math.floor(Math.random() * 1000),
      type,
      title,
      message,
    };

    const current = this.toastsSubject.getValue();
    this.toastsSubject.next([...current, toast]);

    setTimeout(() => this.dismiss(toast.id), 4000);
  }

  dismiss(id: number): void {
    this.toastsSubject.next(this.toastsSubject.getValue().filter((toast) => toast.id !== id));
  }
}
