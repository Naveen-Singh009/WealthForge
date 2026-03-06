export interface Advisor {
  id: number;
  name: string;
  email: string;
  phone: string;
  expertise?: string;
  experienceYears?: number;
}

export interface AllocateAdvisorRequest {
  advisorId: number;
  investorId: number;
}
