export interface StartSessionRequest {
  routineId: number;
  trainingDayId: number;
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
  setNumber: number;
  repsPerformed: number;
  weightLifted: number;
  rir: number;
  notes?: string;
}

export interface SetLogResponse {
  id: number;
  exerciseId: number;
  reps: number;
  weight: number;
  rir: number;
  timestamp: string;
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
}

export interface MemberProgressSummaryResponse {
  exercises: ExerciseSummaryDTO[];
}
