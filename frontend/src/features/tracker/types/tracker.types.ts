export interface StartSessionRequest {
  routineId: number;
  trainingDayId: number;
}

export interface SetLogResponse {
  id: number;
  exerciseId: number;
  setNumber: number;
  repsPerformed: number;
  weightLifted: number;
  rir: number;
  notes?: string;
}

export interface SessionResponse {
  id: number;
  memberId: number;
  routineId: number;
  trainingDayId: number;
  startTime: string;
  endTime: string | null;
  status: 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  loggedSets: SetLogResponse[];
}

export interface LogSetRequest {
  exerciseId: number;
  setNumber: number;
  repsPerformed: number;
  weightLifted: number;
  rir: number;
  notes?: string;
}

export interface ProgressDataPoint {
  date: string;
  e1rm: number;
}

export interface ExerciseProgressResponse {
  exerciseId: number;
  exerciseName: string;
  dataPoints: ProgressDataPoint[];
}

export interface ExerciseSummaryDTO {
  exerciseId: number;
  exerciseName: string;
  currentPersonalRecord: number;
  exerciseImageUrl: string;
}

export interface MemberProgressSummaryResponse {
  exercises: ExerciseSummaryDTO[];
}
