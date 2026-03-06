import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { UserRole } from '../../shared/models/user.model';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class SidebarComponent {
  constructor(private readonly authService: AuthService) {}

  get role(): UserRole | null {
    return this.authService.getRole();
  }
}
