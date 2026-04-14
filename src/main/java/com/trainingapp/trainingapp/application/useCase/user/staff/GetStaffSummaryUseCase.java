package com.trainingapp.trainingapp.application.useCase.user.staff;

import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.web.dto.user.staff.StaffMemberResponse;
import com.trainingapp.trainingapp.web.dto.user.staff.StaffSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetStaffSummaryUseCase {

    private final TrainerRepository trainerRepository;
    private final ReceptionistRepository receptionistRepository;

    public GetStaffSummaryUseCase(TrainerRepository trainerRepository, 
                                 ReceptionistRepository receptionistRepository) {
        this.trainerRepository = trainerRepository;
        this.receptionistRepository = receptionistRepository;
    }

    public StaffSummaryResponse execute(Long gymId, String roleFilter) {
        List<Trainer> trainers = trainerRepository.findByGymId(gymId);
        List<Receptionist> receptionists = receptionistRepository.findAllByGymId(gymId);

        // Calcular estadísticas globales siempre (para los StatCards)
        long totalTrainers = trainers.size();
        long totalReceptionists = receptionists.size();
        long totalStaff = totalTrainers + totalReceptionists;

        // Filtrar la lista según el rol solicitado
        List<StaffMemberResponse> filteredList = new ArrayList<>();

        if (roleFilter == null || roleFilter.equalsIgnoreCase("ALL")) {
            filteredList.addAll(trainers.stream().map(this::mapToStaffMember).collect(Collectors.toList()));
            filteredList.addAll(receptionists.stream().map(this::mapToStaffMember).collect(Collectors.toList()));
        } else if (roleFilter.equalsIgnoreCase("TRAINER")) {
            filteredList.addAll(trainers.stream().map(this::mapToStaffMember).collect(Collectors.toList()));
        } else if (roleFilter.equalsIgnoreCase("RECEPTIONIST")) {
            filteredList.addAll(receptionists.stream().map(this::mapToStaffMember).collect(Collectors.toList()));
        }

        return new StaffSummaryResponse(
            filteredList,
            new StaffSummaryResponse.StaffStats(totalStaff, totalTrainers, totalReceptionists)
        );
    }

    private StaffMemberResponse mapToStaffMember(Trainer t) {
        return new StaffMemberResponse(
            t.getId(), t.getFirstName(), t.getLastName(), t.getEmail(), 
            t.getDni(), Role.TRAINER, t.getSpecialization(), t.isActive()
        );
    }

    private StaffMemberResponse mapToStaffMember(Receptionist r) {
        return new StaffMemberResponse(
            r.getId(), r.getFirstName(), r.getLastName(), r.getEmail(), 
            r.getDni(), Role.RECEPTIONIST, null, r.isActive()
        );
    }
}
