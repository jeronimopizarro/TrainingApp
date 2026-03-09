package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    List<MemberJpaEntity> findByGymId(Long gymId);

    Optional<MemberJpaEntity> findByQrAccessCode(String qrAccessCode);
}