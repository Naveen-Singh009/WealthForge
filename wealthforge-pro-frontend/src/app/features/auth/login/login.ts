import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  submitting = false;
  otpRequired = false;
  otpEmail = '';

  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly otpForm = this.fb.nonNullable.group({
    otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
  });

  get email() {
    return this.loginForm.controls.email;
  }

  get password() {
    return this.loginForm.controls.password;
  }

  get otp() {
    return this.otpForm.controls.otp;
  }

  submit(): void {
    if (this.otpRequired) {
      this.verifyOtp();
      return;
    }

    if (this.loginForm.invalid || this.submitting) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.toastService.show('success', 'Login Successful', 'Welcome back to WealthForge Pro.');
        this.router.navigateByUrl(this.authService.getPostLoginRoute());
      },
      error: (error) => {
        const message =
          error?.error?.message
          ?? error?.message
          ?? 'Invalid credentials. Please try again.';

        if (error?.otpRequired || String(message).toLowerCase().includes('otp')) {
          this.otpRequired = true;
          this.otpEmail = error?.email ?? this.loginForm.controls.email.value;
          this.otpForm.reset({ otp: '' });
          this.toastService.show('warning', 'OTP Verification Required', String(message));
        } else {
          this.toastService.show('error', 'Login Failed', String(message));
        }

        this.loadingService.hide();
        this.submitting = false;
      },
      complete: () => {
        this.loadingService.hide();
        this.submitting = false;
      },
    });
  }

  verifyOtp(): void {
    if (this.otpForm.invalid || this.submitting) {
      this.otpForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    this.authService.verifyLoginOtp({
      email: this.otpEmail || this.loginForm.controls.email.value,
      otp: this.otpForm.controls.otp.value,
    }).subscribe({
      next: () => {
        this.toastService.show('success', 'Login Successful', 'OTP verified successfully.');
        this.router.navigateByUrl(this.authService.getPostLoginRoute());
      },
      error: (error) => {
        const message =
          error?.error?.message
          ?? error?.message
          ?? 'Invalid OTP. Please try again.';
        this.toastService.show('error', 'OTP Verification Failed', String(message));
      },
      complete: () => {
        this.loadingService.hide();
        this.submitting = false;
      },
    });
  }

  backToPasswordLogin(): void {
    this.otpRequired = false;
    this.otpEmail = '';
    this.otpForm.reset({ otp: '' });
  }
}

