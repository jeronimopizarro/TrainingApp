export interface StartSessionRequest {
  routineDayId: number;
}

export interface SessionResponse {
  id: number;
  routineDayId: number;
  startTime: string;
  endTime: string | null;
  status: 'IN_PROGRESS' | 'FINISHED';
}

export interface LogSetRequest {
  exerciseId: number;
  reps: number;
  weight: number;
  rir: number;
}

export interface SetLogResponse {
  id: number;
  exerciseId: number;
  reps: number;
  weight: number;
  rir: number;
  timestamp: string;
}
