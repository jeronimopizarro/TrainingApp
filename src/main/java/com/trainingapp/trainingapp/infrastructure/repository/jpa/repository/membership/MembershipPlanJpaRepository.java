package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.membership;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.membership.MembershipPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanJpaRepository extends JpaRepository<MembershipPlanJpaEntity, Long> {

    List<MembershipPlanJpaEntity> findByGymIdAndActiveTrue(Long gymId);

    Optional<MembershipPlanJpaEntity> findByIdAndActiveTrue(Long id);

    boolean existsByNameAndGymIdAndActiveTrue(String name, Long gymId);

    boolean existsByNameAndGymIdAndIdNotAndActiveTrue(String name, Long gymId, Long id);
}