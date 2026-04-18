package com.trainingapp.trainingapp.web.controller.tracker;

import com.trainingapp.trainingapp.application.useCase.tracker.GetExerciseProgressUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.GetMemberProgressSummaryUseCase;
import com.trainingapp.trainingapp.web.dto.tracker.ExerciseProgressResponse;
import com.trainingapp.trainingapp.web.dto.tracker.MemberProgressSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class MemberProgressController {

    private final GetMemberProgressSummaryUseCase getMemberProgressSummaryUseCase;
    private final GetExerciseProgressUseCase getExerciseProgressUseCase;

    public MemberProgressController(GetMemberProgressSummaryUseCase getMemberProgressSummaryUseCase,
                                    GetExerciseProgressUseCase getExerciseProgressUseCase) {
        this.getMemberProgressSummaryUseCase = getMemberProgressSummaryUseCase;
        this.getExerciseProgressUseCase = getExerciseProgressUseCase;
    }

    /**
     * Obtiene el listado de todos los ejercicios realizados por el miembro con su mejor récord promedio.
     * Ideal para la pantalla general de "Progreso".
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'GYM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MemberProgressSummaryResponse> getMemberProgressSummary(
            @RequestParam(required = false) Long memberId) {
        MemberProgressSummaryResponse response = getMemberProgressSummaryUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }


    /**
     * Obtiene el historial detallado de un ejercicio específico para graficar.
     * Permite filtrar cuántos meses hacia atrás se desea consultar.
     */
    @GetMapping("/exercise/{exerciseId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'GYM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ExerciseProgressResponse> getExerciseProgress(
            @PathVariable Long exerciseId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "6") int monthsBack) {

        ExerciseProgressResponse
                response = getExerciseProgressUseCase.execute(exerciseId, memberId, monthsBack);
        return ResponseEntity.ok(response);
    }
}