import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { ToastService } from '../../../core/services/toast.service';

type AdminCreatableRole = 'ADMIN' | 'ADVISOR';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
})
export class AdminDashboardComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly portfolioService = inject(PortfolioService);
  private readonly loadingService = inject(LoadingService);
  private readonly toastService = inject(ToastService);

  readonly roles: AdminCreatableRole[] = ['ADMIN', 'ADVISOR'];
  submitting = false;
  totalPortfolios = 0;

  readonly createUserForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['ADMIN' as AdminCreatableRole, [Validators.required]],
    phone: [''],
  });

  ngOnInit(): void {
    this.loadPortfolioCount();
  }

  loadPortfolioCount(): void {
    this.portfolioService.getAdminPortfolios().subscribe({
      next: (response) => {
        this.totalPortfolios = response.data?.length ?? 0;
      },
      error: () => {
        this.totalPortfolios = 0;
      },
    });
  }

  submitCreateUser(): void {
    if (this.createUserForm.invalid || this.submitting) {
      this.createUserForm.markAllAsTouched();
      return;
    }

    const role = this.createUserForm.controls.role.value;
    const phone = this.createUserForm.controls.phone.value.trim();

    if (role === 'ADVISOR' && !phone) {
      this.createUserForm.controls.phone.setErrors({ required: true });
      this.createUserForm.controls.phone.markAsTouched();
      return;
    }

    this.submitting = true;
    this.loadingService.show();

    this.authService.createUserByAdmin({
      name: this.createUserForm.controls.name.value,
      email: this.createUserForm.controls.email.value,
      password: this.createUserForm.controls.password.value,
      role,
      phone: role === 'ADVISOR' ? phone : undefined,
    }).subscribe({
      next: (response) => {
        this.toastService.show('success', 'User Created', response.message);
        this.createUserForm.reset({
          name: '',
          email: '',
          password: '',
          role: 'ADMIN',
          phone: '',
        });
      },
      error: (error) => {
        const message = error?.error?.message ?? 'Unable to create user.';
        this.toastService.show('error', 'Create Failed', message);
      },
      complete: () => {
        this.loadingService.hide();
        this.submitting = false;
      },
    });
  }

  get isAdvisorSelected(): boolean {
    return this.createUserForm.controls.role.value === 'ADVISOR';
  }
}

