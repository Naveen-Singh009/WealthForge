import { Component } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class NavbarComponent {
  constructor(private readonly authService: AuthService) {}

  get roleLabel(): string {
    return this.authService.getCurrentRole() ?? 'Guest';
  }

  logout(): void {
    this.authService.logout();
  }
}
