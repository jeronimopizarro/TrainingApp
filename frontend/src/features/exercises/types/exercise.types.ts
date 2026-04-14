export interface MuscleGroup {
  id: number;
  name: string;
  description?: string;
}

export interface ExerciseMuscleGroupDetail {
  muscleGroupId: number;
  isPrimary: boolean;
}

export interface Exercise {
  id: number;
  name: string;
  description: string;
  imageUrl?: string;
  videoUrl?: string;
  isBase: boolean;
  creatorTrainerId?: number;
  muscleGroups: ExerciseMuscleGroupDetail[];
}

export interface CreateExerciseRequest {
  name: string;
  description: string;
  imageUrl?: string;
  videoUrl?: string;
  isBase: boolean;
  muscleGroups: ExerciseMuscleGroupDetail[];
}

export interface UpdateExerciseRequest {
  name: string;
  description: string;
  imageUrl?: string;
  videoUrl?: string;
  muscleGroups: ExerciseMuscleGroupDetail[];
}
