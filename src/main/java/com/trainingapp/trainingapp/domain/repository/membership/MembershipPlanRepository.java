package com.trainingapp.trainingapp.domain.repository.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository {
    MembershipPlan save(MembershipPlan plan);

    Optional<MembershipPlan> findById(Long id);

    List<MembershipPlan> findByGymId(Long gymId);

    boolean existsByNameAndGymId(String name, Long gymId);

    boolean existsByNameAndGymIdAndIdNot(String name, Long gymId, Long id);
}