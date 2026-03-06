export type UserRole = 'ADMIN' | 'INVESTOR' | 'ADVISOR';

export interface CurrentUser {
  id: number;
  name: string;
  email: string;
  role: UserRole;
}
