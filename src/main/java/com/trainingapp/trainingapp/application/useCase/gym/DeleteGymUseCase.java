package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteGymUseCase {

    private final GymRepository gymRepository;
    private final AdminRepository adminRepository;
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;

    public DeleteGymUseCase(GymRepository gymRepository, AdminRepository adminRepository,
                            TrainerRepository trainerRepository, MemberRepository memberRepository) {
        this.gymRepository = gymRepository;
        this.adminRepository = adminRepository;
        this.trainerRepository = trainerRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void execute(Long id) {
        Gym gym = findGymOrThrow(id);

        deactivateAndSaveGym(gym);
        deactivateAndSaveAdmins(id);
        deactivateAndSaveTrainers(id);
        deactivateAndSaveMembers(id);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));
    }

    private void deactivateAndSaveGym(Gym gym) {
        gym.deactivate();
        gymRepository.save(gym);
    }

    private void deactivateAndSaveAdmins(Long gymId) {
        List<Admin> admins = adminRepository.findByGymId(gymId);
        admins.forEach(admin -> {
            admin.deactivate();
            adminRepository.save(admin);
        });
    }

    private void deactivateAndSaveTrainers(Long gymId) {
        List<Trainer> trainers = trainerRepository.findByGymId(gymId);
        trainers.forEach(trainer -> {
            trainer.deactivate();
            trainerRepository.save(trainer);
        });
    }

    private void deactivateAndSaveMembers(Long gymId) {
        List<Member> members = memberRepository.findByGymId(gymId);
        members.forEach(member -> {
            member.deactivate();
            memberRepository.save(member);
        });
    }
}
