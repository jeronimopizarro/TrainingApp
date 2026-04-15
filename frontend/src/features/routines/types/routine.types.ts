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
  endDate: string;
  memberId: number;
  trainerId: number;
  createdByUserId: number;
  status: RoutineStatus;
  days: DayDetail[];
}

export interface RoutineSummary {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  status: RoutineStatus;
  trainerName: string;
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
}
