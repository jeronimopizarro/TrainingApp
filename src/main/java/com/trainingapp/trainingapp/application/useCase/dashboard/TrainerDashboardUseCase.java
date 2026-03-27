package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.dashboard.TrainerDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.TrainerDashboardResponse.PendingRoutineRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerDashboardUseCase {

    private final SecurityUtils securityUtils;
    private final TrainerRepository trainerRepository;
    private final RoutineRequestRepository routineRequestRepository;
    private final MemberRepository memberRepository;

    public TrainerDashboardUseCase(SecurityUtils securityUtils,
                                   TrainerRepository trainerRepository,
                                   RoutineRequestRepository routineRequestRepository,
                                   MemberRepository memberRepository) {
        this.securityUtils = securityUtils;
        this.trainerRepository = trainerRepository;
        this.routineRequestRepository = routineRequestRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public TrainerDashboardResponse execute() {
        Long trainerId = securityUtils.getCurrentUser().getId();
        Trainer trainer = getTrainerById(trainerId);

        List<RoutineRequest> pendingRequests = getPendingRequestsForGym(trainer.getGymId());

        List<PendingRoutineRequestDTO> requestDTOs = mapRequestsToDTOs(pendingRequests);

        return new TrainerDashboardResponse(requestDTOs);
    }

    private Trainer getTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new TrainerNotFoundException("No se encontró el entrenador con ID: " + trainerId));
    }

    private List<RoutineRequest> getPendingRequestsForGym(Long gymId) {
        return routineRequestRepository.findByGymIdAndStatus(gymId, RoutineRequestStatus.PENDING);
    }

    private List<PendingRoutineRequestDTO> mapRequestsToDTOs(List<RoutineRequest> pendingRequests) {
        return pendingRequests.stream()
                .map(this::toDTO)
                .toList();
    }

    private PendingRoutineRequestDTO toDTO(RoutineRequest request) {
        // Buscamos el nombre del socio; si por algún error no existe, devolvemos un texto por defecto
        String memberFullName = memberRepository.findById(request.getMemberId())
                .map(member -> member.getFirstName() + " " + member.getLastName())
                .orElse("Socio Desconocido");

        return new PendingRoutineRequestDTO(
                request.getId(),
                request.getMemberId(),
                memberFullName,
                request.getNote(),
                request.getRequestDate()
        );
    }
}