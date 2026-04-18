export type RoutineStatus = 'ACTIVE' | 'INACTIVE' | 'COMPLETED' | 'REQUESTED';
export type ExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export interface ExerciseItem {
  orderNumber: number;
  sets: number;
  repsMin: number;
  repsMax: number;
  targetRIR: number;
  suggestedWeight: number;
  notes: string;
  exerciseId: number;
  exerciseName: string;
  exerciseImageUrl: string;
  exerciseVideoUrl: string;
}

export interface DayDetail {
  id: number;
  name: string;
  orderNumber: number;
  exercises: ExerciseItem[];
}

export interface RoutineDetail {
  id: number;
  name: string;
  startDate: string;
  endDate: string | null;
  memberId: number | null;
  trainerId: number;
  createdByUserId: number;
  status: RoutineStatus;
  isBase: boolean;
  days: DayDetail[];
}

export interface RoutineSummary {
  id: number;
  name: string;
  startDate: string;
  endDate: string | null;
  status: RoutineStatus;
  trainerName: string;
  memberName?: string; // Para la vista del trainer
  memberId?: number | null;
  isBase?: boolean;
}

export interface CreateBaseRoutineRequest {
  name: string;
  days: CreateTrainingDayRequest[];
}

export interface RoutineRequestSummary {
  id: number;
  memberId: number;
  memberName: string;
  requestDate: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  targetTrainerId: number | null;
  availableDays: number;
  experienceLevel: ExperienceLevel;
  injuries: string;
  primaryGoal: string;
}

export interface AssignRoutineRequest {
  memberId: number;
  requestId?: number | null;
  name: string;
  durationMonths: number;
  days: CreateTrainingDayRequest[];
}

export interface DuplicateRoutineRequest {
  targetMemberId: number;
  newRoutineName: string;
}

export interface RequestRoutineMessage {
  targetTrainerId?: number | null;
  availableDays: number;
  experienceLevel: ExperienceLevel;
  injuries?: string;
  primaryGoal: string;
}

export interface CreateRoutineDetailRequest {
  exerciseId: number;
  sets: number;
  repsMin: number;
  repsMax: number;
  targetRIR: number;
  suggestedWeight: number;
  notes?: string;
}

export interface CreateTrainingDayRequest {
  dayName: string;
  exercises: CreateRoutineDetailRequest[];
}

export interface CreatePersonalRoutineRequest {
  name: string;
  days: CreateTrainingDayRequest[];
  durationMonths?: number | null;
}
