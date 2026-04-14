export type StaffRole = 'TRAINER' | 'RECEPTIONIST';

export interface BaseStaffMember {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  dni: string;
  gymId: number;
  role: StaffRole;
  active: boolean;
}

export interface Trainer extends BaseStaffMember {
  role: 'TRAINER';
  specialization: string;
}

export interface Receptionist extends BaseStaffMember {
  role: 'RECEPTIONIST';
}

export type StaffMember = Trainer | Receptionist;

export interface StaffStats {
  total: number;
  trainers: number;
  receptionists: number;
}

export interface StaffSummaryResponse {
  staffMembers: StaffMember[];
  stats: StaffStats;
}

export interface RegisterStaffRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  dni: string;
  gymId: number;
  role: StaffRole;
  specialization?: string; // Only for Trainer
}

export interface UpdateStaffRequest {
  firstName: string;
  lastName: string;
  dni: string;
  specialization?: string; // Only for Trainer
}
