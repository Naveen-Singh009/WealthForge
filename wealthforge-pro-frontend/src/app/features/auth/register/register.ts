import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  submitting = false;
  readonly roles = ['INVESTOR'] as const;

  readonly registerForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['INVESTOR' as const, [Validators.required]],
  });

  submit(): void {
    if (this.registerForm.invalid || this.submitting) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.toastService.show('success', 'Registration Successful', 'Account created. Please login.');
        this.router.navigateByUrl('/login');
      },
      error: () => {
        this.toastService.show('error', 'Registration Failed', 'Unable to create account with provided details.');
        this.loadingService.hide();
        this.submitting = false;
      },
      complete: () => {
        this.loadingService.hide();
        this.submitting = false;
      },
    });
  }
}
