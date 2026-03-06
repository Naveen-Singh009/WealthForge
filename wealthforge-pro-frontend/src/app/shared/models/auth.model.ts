import { CurrentUser } from './user.model';

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: 'INVESTOR';
}

export interface AdminCreateUserRequest {
  name: string;
  email: string;
  password: string;
  role: 'ADMIN' | 'ADVISOR';
  phone?: string;
}

export interface MessageResponse {
  message: string;
}

export interface VerifyOtpRequest {
  email: string;
  otp: string;
}

export interface AuthResponse {
  token: string;
  type?: string;
  email?: string;
  role?: string;
  expiresIn?: number;
  user?: CurrentUser;
}
