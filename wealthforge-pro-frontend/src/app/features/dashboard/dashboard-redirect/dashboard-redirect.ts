import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-redirect',
  standalone: false,
  template: '',
})
export class DashboardRedirectComponent {
  constructor(private readonly authService: AuthService, private readonly router: Router) {
    this.router.navigateByUrl(this.authService.getPostLoginRoute());
  }
}
