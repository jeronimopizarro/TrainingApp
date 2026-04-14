export interface AccessLogResponse {
  id: number;
  memberId: number;
  memberFirstName: string;
  memberLastName: string;
  timestamp: string;
  accessGranted: boolean;
  message: string;
}

export interface GymAccessSummaryResponse {
  totalSuccessfulEntriesToday: number;
  totalFailedAttemptsToday: number;
  logs: AccessLogResponse[];
}

export interface ValidateAccessRequest {
  identifier: string;
  method: 'DNI' | 'QR';
}

export interface ValidateAccessResponse {
  accessGranted: boolean;
  memberName: string;
  message: string;
}
