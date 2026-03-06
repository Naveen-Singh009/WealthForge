import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AdminCreateUserRequest,
  AuthRequest,
  AuthResponse,
  MessageResponse,
  RegisterRequest,
  VerifyOtpRequest,
} from '../../shared/models/auth.model';
import { CurrentUser, UserRole } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly authBaseUrl = environment.authBaseUrl ?? '';
  private readonly tokenKey = 'wf_token';
  private readonly roleKey = 'wf_role';
  private readonly userKey = 'wf_user';

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(payload: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse | MessageResponse>(`${this.authBaseUrl}/auth/login`, payload).pipe(
      map((response) => {
        const authResponse = response as AuthResponse;

        if (typeof authResponse?.token === 'string' && authResponse.token.trim().length > 0) {
          return this.normalizeAuthResponse(authResponse, payload.email);
        }

        const message = (response as MessageResponse)?.message ?? 'Login response did not include a valid token.';
        throw {
          otpRequired: true,
          message,
          email: payload.email,
        };
      }),
      tap((response) => this.persistSession(response))
    );
  }

  verifyLoginOtp(payload: VerifyOtpRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authBaseUrl}/auth/verify-login-otp`, payload).pipe(
      map((response) => this.normalizeAuthResponse(response, payload.email)),
      tap((response) => this.persistSession(response))
    );
  }

  register(payload: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.authBaseUrl}/auth/register`, payload);
  }

  createUserByAdmin(payload: AdminCreateUserRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.authBaseUrl}/api/admin/users`, payload);
  }

  logout(redirectToLogin = true): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.userKey);

    if (redirectToLogin) {
      this.router.navigate(['/login']);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn();
  }

  getRole(): UserRole | null {
    const persistedRole = this.normalizeRoleClaim(localStorage.getItem(this.roleKey));
    if (persistedRole) {
      return persistedRole;
    }

    const user = this.getCurrentUser();
    if (user?.role) {
      return user.role;
    }

    const decodedRole = this.extractRoleFromToken(this.getToken());
    if (decodedRole) {
      localStorage.setItem(this.roleKey, decodedRole);
    }

    return decodedRole;
  }

  getCurrentRole(): UserRole | null {
    return this.getRole();
  }

  getCurrentUser(): CurrentUser | null {
    const raw = localStorage.getItem(this.userKey);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as CurrentUser;
    } catch {
      return null;
    }
  }

  getCurrentUserId(): number | null {
    return this.getCurrentUser()?.id ?? this.extractUserIdFromToken(this.getToken());
  }

  hasRole(role: UserRole): boolean {
    return this.getRole() === role;
  }

  private persistSession(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);

    const role = response.user?.role
      ?? this.normalizeRoleClaim(response.role)
      ?? this.extractRoleFromToken(response.token);
    if (role) {
      localStorage.setItem(this.roleKey, role);
    } else {
      localStorage.removeItem(this.roleKey);
    }

    if (response.user) {
      localStorage.setItem(this.userKey, JSON.stringify(response.user));
    } else {
      localStorage.removeItem(this.userKey);
    }
  }

  private isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload) {
      return true;
    }

    if (typeof payload['exp'] !== 'number') {
      return true;
    }

    const expiresAt = payload['exp'] * 1000;
    return Date.now() > expiresAt;
  }

  private normalizeRoleClaim(claim: string | null | undefined): UserRole | null {
    if (!claim) {
      return null;
    }

    const normalized = claim.replace('ROLE_', '').toUpperCase();
    if (normalized === 'ADMIN' || normalized === 'INVESTOR' || normalized === 'ADVISOR') {
      return normalized as UserRole;
    }

    return null;
  }

  private extractRoleFromToken(token: string | null): UserRole | null {
    if (!token) {
      return null;
    }

    const payload = this.decodeToken(token);
    if (!payload) {
      return null;
    }

    const claimCandidates: unknown[] = [
      payload['role'],
      payload['roles'],
      payload['authorities'],
      payload['authority'],
    ];

    for (const claim of claimCandidates) {
      const normalizedClaimValues = Array.isArray(claim) ? claim : [claim];

      for (const value of normalizedClaimValues) {
        if (typeof value !== 'string') {
          continue;
        }

        const normalized = this.normalizeRoleClaim(value);
        if (normalized) {
          return normalized;
        }
      }
    }

    return null;
  }

  private extractUserIdFromToken(token: string | null): number | null {
    if (!token) {
      return null;
    }

    const payload = this.decodeToken(token);
    const userId = payload?.['userId'];

    return typeof userId === 'number' ? userId : null;
  }

  private decodeToken(token: string): Record<string, unknown> | null {
    const chunks = token.split('.');
    if (chunks.length < 2) {
      return null;
    }

    try {
      const normalizedPayload = chunks[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/')
        .padEnd(Math.ceil(chunks[1].length / 4) * 4, '=');
      const json = decodeURIComponent(
        window.atob(normalizedPayload)
          .split('')
          .map((char) => `%${`00${char.charCodeAt(0).toString(16)}`.slice(-2)}`)
          .join('')
      );
      return JSON.parse(json) as Record<string, unknown>;
    } catch {
      return null;
    }
  }

  getPostLoginRoute(): string {
    const role = this.getRole();

    switch (role) {
      case 'ADMIN':
        return '/admin/dashboard';
      case 'ADVISOR':
        return '/advisor/dashboard';
      case 'INVESTOR':
      default:
        return '/investor/dashboard';
    }
  }

  getAuthRedirectIfLoggedIn(): Observable<boolean> {
    return new Observable<boolean>((subscriber) => {
      subscriber.next(this.isAuthenticated());
      subscriber.complete();
    }).pipe(map(Boolean));
  }

  private normalizeAuthResponse(response: AuthResponse, fallbackEmail: string): AuthResponse {
    if (response.user) {
      return response;
    }

    const derivedRole = this.normalizeRoleClaim(response.role) ?? this.extractRoleFromToken(response.token);
    const derivedId = this.extractUserIdFromToken(response.token);
    const email = response.email ?? fallbackEmail;

    if (!derivedRole || !derivedId) {
      return response;
    }

    const user: CurrentUser = {
      id: derivedId,
      email,
      role: derivedRole,
      name: email.split('@')[0],
    };

    return {
      ...response,
      user,
    };
  }
}
