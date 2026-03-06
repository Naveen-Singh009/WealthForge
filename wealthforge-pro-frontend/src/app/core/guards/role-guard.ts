import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { UserRole } from '../../shared/models/user.model';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  const allowedRoles = (route.data['roles'] as UserRole[] | undefined) ?? [];
  const currentRole = authService.getRole();

  if (!currentRole) {
    router.navigate(['/login']);
    return false;
  }

  if (allowedRoles.length === 0 || allowedRoles.includes(currentRole)) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
